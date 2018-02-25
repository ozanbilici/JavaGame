package message;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	private String code;
	private String msg;
	private int number;
	private int number2;
	private int[][] data;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public int[][] getData() {
		return data;
	}
	
	public void setData(int[][] data) {
	    if (data == null) {
	        return;
	    }
	    
	    this.data = new int[data.length][];
	    
	    for (int i = 0; i < data.length; i++) {
	        this.data[i] = Arrays.copyOf(data[i], data[i].length);
	    }
	}
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getNumber2() {
		return number2;
	}
	public void setNumber2(int number2) {
		this.number2 = number2;
	}
	
	
}
