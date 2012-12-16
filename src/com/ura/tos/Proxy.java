package com.ura.tos;

public class Proxy {
	private String id;
	private Integer fromport;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getFromport() {
		return fromport;
	}

	public void setFromport(Integer fromport) {
		this.fromport = fromport;
	}

	public Integer getToport() {
		return toport;
	}

	public void setToport(Integer toport) {
		this.toport = toport;
	}

	public String getToip() {
		return toip;
	}

	public void setToip(String toip) {
		this.toip = toip;
	}

	private Integer toport;
	private String toip;
}
