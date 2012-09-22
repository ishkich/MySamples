package com.example.machinechecker;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;

import android.util.Log;

import com.example.machinechecker.Result.EResult;

/**
 * ネットワーク関連のツール類。 INTERNET USES Permissionが必要。
 */
public class NetworkTools {
	private static final String TAG = "NetworkTools";
	private String host = "localhost";
	private InetAddress addr = null;
	private Integer port = null;

	private int state = 0;

	public NetworkTools(String host) {
		this.host = host;
	}
	
	public NetworkTools(String host, Integer port) {
		this.host = host;
		this.port = port;
	}

	public Result next() {
		Result ret = null;
		
		switch(state) {
		case 0:
			Log.d(TAG, "state: " + state);
			ret = resolve();
			if(ret != null && ret.succeeds()) {
				state++;
			} else {
				state=-1;
			}
			break;
		case 1:
			Log.d(TAG, "state: " + state);
			ret = reachable();
			if(ret != null && ret.succeeds()) {
				state++;
			} else {
				state=-1;
			}
			break;
		default:
			Log.d(TAG, "no more test");
			ret = new Result("no more test");
			ret.setResult(EResult.FINISHED);
		}
		
		return ret;
	}
	
	/**
	 * DNS lookup
	 * 
	 * @param arg
	 * @return もし名前解決に成功すればarg.addrにInetAddressを保存し、msecにRTTを設定する。
	 */
	private Result resolve() {
		Result result = new Result("DNS lookup");
		
		Log.d(TAG, "try to resolve hostname: " + host);
		try {
			long start = getMS();
			InetAddress[] inets = InetAddress.getAllByName(host);
			long end = getMS();

			for (InetAddress inet : inets) {
				if (!(inet instanceof Inet4Address)) {
					continue;
				}
				addr = inet;
				setSuccessful(result, start, end, 
						inet.getHostAddress());
			}
		} catch (UnknownHostException e) {
			Log.i(TAG, "cannot resolve hostname: " + host);
		}

		return result;
	}

	/**
	 * addr(:port)に接続可能か判定する。
	 * コンストラクタの引数によりICMP PINGかTCP接続かを選ぶ。
	 * 
	 * @param arg
	 * @return
	 */
	public Result reachable() {
		Result result=new Result("reachable");
		// 名前解決できなかった場合は何もしない。
		if (addr == null) {
			return result;
		}

		if (port == null) {
			result = icmpReachable();
		} else {
			result = connectReachable();
		}
		
		return result;
	}

	private Result icmpReachable() {
		Result result = new Result("icmp ping");
		
		Log.i(TAG, "try to ping hostname: " + host);
		try {
			long start=getMS();
			boolean isReachable = addr.isReachable(1000);
			long end=getMS();
			if(isReachable) {
				setSuccessful(result, start, end, "ping succeed");
			}
		} catch (IOException e) {
			Log.i(TAG, "cannot ping hostname: " + host);
		}
		return result;
	}
	
	private Result connectReachable() {
		Result result = new Result("tcp connect " + host + ":" + port);
		
		Log.i(TAG, "try to connect hostname: " + host + ":" + port);
		try {
			long start=getMS();
			Socket s = new Socket(addr, port);
			long end=getMS();
			s.close();
			setSuccessful(result, start, end, "connected");
		} catch (IOException e) {
			Log.i(TAG, "cannot connect");
		}
		
		return result;
	}
	
	// internal library
	
	private long getMS() {
		return Calendar.getInstance().getTimeInMillis();
	}
	
	private void setSuccessful(Result r, long start, long end, String msg) {
		r.setResult(EResult.SUCCESS);
		r.setMsec(end-start);
		r.setComment(msg);
	}
}
