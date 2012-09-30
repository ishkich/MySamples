package com.example.machinechecker;

public class Result {
	enum EResult {
		SUCCESS, 
		FAIL,
		NA,
		FINISHED
	};
	
	/** 試験名 */
	private String testName;
	/** 試験結果 */
	private EResult result;
	/** 試験結果に対するコメント */
	private String comment;
	/** 試験にかかった時間 */
	private long msec;
	
	public Result(String testName) {
		super();
		this.testName = testName;
		this.result = EResult.FAIL;
		this.comment = "";
		this.msec = -1;
	}

	public boolean succeeds() {
		return result.equals(EResult.SUCCESS) ? true : false;
	}
	public boolean finished() {
		return result.equals(EResult.FINISHED) ? true : false;
	}
	
	// primitive getter/setter
	
	public EResult getResult() {
		return result;
	}
	public void setResult(EResult result) {
		this.result = result;
	}
	public String getTestName() {
		return testName;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public long getMsec() {
		return msec;
	}
	public void setMsec(long msec) {
		this.msec = msec;
	}

}
