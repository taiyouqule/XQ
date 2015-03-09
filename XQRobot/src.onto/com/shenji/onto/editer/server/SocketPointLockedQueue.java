package com.shenji.onto.editer.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SocketPointLockedQueue {
	public HashMap<String, PointInfBean> pointLocedQueue;

	public SocketPointLockedQueue() {
		this.pointLocedQueue = new HashMap<String, SocketPointLockedQueue.PointInfBean>();
	}

	public void add(String pointName, int pointType, String pointHolder,
			String message) {
		PointInfBean infBean = new PointInfBean();
		infBean.setPointType(pointType);
		infBean.setPointHolder(pointHolder);
		infBean.setMessage(message);
		this.pointLocedQueue.put(pointName, infBean);
	}

	public String[] getLockedQueueMessages() {
		List<String> messageList = new ArrayList<String>();
		Iterator<Entry<String, PointInfBean>> iterator = this.pointLocedQueue
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, PointInfBean> entry = (Map.Entry<String, SocketPointLockedQueue.PointInfBean>) iterator
					.next();
			PointInfBean value = entry.getValue();
			messageList.add(value.getMessage());
		}
		return (String[]) messageList.toArray(new String[messageList.size()]);

	}

	public boolean delete(String pointName) {
		Object obj = this.pointLocedQueue.remove(pointName);
		if (obj == null)
			return false;
		return true;
	}

	public void clear() {
		this.pointLocedQueue.clear();
	}

	public String[] removeHistoryByHolder(String pointHolder) {
		List<String> messageList = new ArrayList<String>();
		Iterator<Entry<String, PointInfBean>> iterator = this.pointLocedQueue
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, PointInfBean> entry = (Map.Entry<String, SocketPointLockedQueue.PointInfBean>) iterator
					.next();
			PointInfBean value = entry.getValue();
			if (value.getPointHolder().equals(pointHolder)) {
				iterator.remove();
				messageList.add(lockedToReleaseMessage(value.getMessage()));
			}
		}
		return (String[]) messageList.toArray(new String[messageList.size()]);
	}

	private String lockedToReleaseMessage(String locedMessage) {
		String releaseMessage = locedMessage.replaceFirst("L", "R");
		return releaseMessage;
	}

	public class PointInfBean {
		private int pointType;
		private String pointHolder;
		private String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public int getPointType() {
			return pointType;
		}

		public void setPointType(int pointType) {
			this.pointType = pointType;
		}

		public String getPointHolder() {
			return pointHolder;
		}

		public void setPointHolder(String pointHolder) {
			this.pointHolder = pointHolder;
		}

	}
}
