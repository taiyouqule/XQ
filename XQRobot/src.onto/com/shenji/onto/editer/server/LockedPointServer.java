package com.shenji.onto.editer.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.shenji.common.log.Log;
import com.shenji.onto.OntologyCommon;
import com.shenji.onto.Configuration;

public class LockedPointServer {
	public static final int CLASS = OntologyCommon.DataType.namedClass;
	public static final int OBJECT_PRO = OntologyCommon.DataType.objectProperty;
	public static final int DATA_PRO = OntologyCommon.DataType.dataTypeProperty;
	public static final int INDIVIDUAL = OntologyCommon.DataType.individual;

	private HashMap<String, String> classPointLockedQueue = null;
	private HashMap<String, String> objectLockedQueue = null;
	private HashMap<String, String> dataPointLockedQueue = null;
	private HashMap<String, String> individualPointLockedQueue = null;

	public LockedPointServer() {
		this.classPointLockedQueue = new HashMap<String, String>();
		// classPointLockedQueue.put("subClass", "121");
		this.objectLockedQueue = new HashMap<String, String>();
		this.dataPointLockedQueue = new HashMap<String, String>();
		this.individualPointLockedQueue = new HashMap<String, String>();
	}

	public synchronized boolean add(String pointName, String pointHolder,
			int pointType) {
		if (this.classPointLockedQueue.containsKey(pointName)
				|| this.objectLockedQueue.containsKey(pointName)
				|| this.dataPointLockedQueue.containsKey(pointName)
				|| this.individualPointLockedQueue.containsKey(pointName))
			return false;
		else {
			Log.getLogger(this.getClass()).debug(
					"锁节点队列L:pointName:" + pointName + "--pointHolder:"
							+ pointHolder);
			switch (pointType) {
			case CLASS:
				this.classPointLockedQueue.put(pointName, pointHolder);
				break;
			case OBJECT_PRO:
				this.objectLockedQueue.put(pointName, pointHolder);
				break;
			case DATA_PRO:
				this.dataPointLockedQueue.put(pointName, pointHolder);
				break;
			case INDIVIDUAL:
				this.individualPointLockedQueue.put(pointName, pointHolder);
				break;
			}
			return true;
		}

	}

	public synchronized boolean release(String pointName, int pointType,
			String pointHolder) {

		if ((this.classPointLockedQueue.containsKey(pointName) && this.classPointLockedQueue
				.get(pointName).equals(pointHolder))
				|| (this.objectLockedQueue.containsKey(pointName) && this.objectLockedQueue
						.get(pointName).equals(pointHolder))
				|| (this.dataPointLockedQueue.containsKey(pointName) && this.dataPointLockedQueue
						.get(pointName).equals(pointHolder))
				|| (this.individualPointLockedQueue.containsKey(pointName) && this.individualPointLockedQueue
						.get(pointName).equals(pointHolder))) {
			Log.getLogger(this.getClass()).debug(
					"释放节点队列：R:pointName:" + pointName + "--pointType:"
							+ pointType);
			switch (pointType) {
			case CLASS:
				this.classPointLockedQueue.remove(pointName);
				break;
			case OBJECT_PRO:
				this.objectLockedQueue.remove(pointName);
				break;
			case DATA_PRO:
				this.dataPointLockedQueue.remove(pointName);
				break;
			case INDIVIDUAL:
				this.individualPointLockedQueue.remove(pointName);
				break;
			}
			return true;
		}
		return false;
	}

	public void close() {
		this.classPointLockedQueue.clear();
		this.objectLockedQueue.clear();
		this.dataPointLockedQueue.clear();
		this.individualPointLockedQueue.clear();
	}

	private void removeHistoryByHolder(HashMap<String, String> hashMap,
			int pointType, String pointHolder, List<String> msgList) {
		// List<String> messageList=new ArrayList<String>();
		Iterator<Entry<String, String>> iterator = hashMap.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator
					.next();
			String key = entry.getKey();
			String value = entry.getValue();
			if (value.equals(pointHolder)) {
				iterator.remove();
				release(key, pointType, value);
				msgList.add(createReleasePointMessage(pointType, key, value));
			}
		}
	}

	public synchronized String[] removeHistoryByHolder(String pointHolder) {
		List<String> msgList = new ArrayList<String>();
		removeHistoryByHolder(this.classPointLockedQueue,
				OntologyCommon.DataType.namedClass, pointHolder, msgList);
		removeHistoryByHolder(this.objectLockedQueue,
				OntologyCommon.DataType.objectProperty, pointHolder, msgList);
		removeHistoryByHolder(this.dataPointLockedQueue,
				OntologyCommon.DataType.dataTypeProperty, pointHolder, msgList);
		removeHistoryByHolder(this.individualPointLockedQueue,
				OntologyCommon.DataType.individual, pointHolder, msgList);
		return (String[]) msgList.toArray(new String[msgList.size()]);

	}

	public synchronized boolean checkIsLocked(String pointName,
			String pointHolder, int pointType) {
		switch (pointType) {
		case CLASS:
			return cheackLocked(this.classPointLockedQueue, pointName,
					pointHolder);
		case OBJECT_PRO:
			return cheackLocked(this.objectLockedQueue, pointName, pointHolder);
		case DATA_PRO:
			return cheackLocked(this.dataPointLockedQueue, pointName,
					pointHolder);
		case INDIVIDUAL:
			return cheackLocked(this.individualPointLockedQueue, pointName,
					pointHolder);
		}
		return false;
	}

	private boolean cheackLocked(HashMap<String, String> map, String pointName,
			String pointHolder) {
		if (map.containsKey(pointName)
				&& !map.get(pointName).equals(pointHolder))
			return true;
		else
			return false;
	}

	public static String createLockPointMessage(int pointType,
			String pointName, String userName) {
		String message = null;
		message = "L" + Configuration.messageSeparator + pointName
				+ Configuration.messageSeparator + pointType
				+ Configuration.messageSeparator + userName;
		return message;

	}

	// 创建释放节点锁消息
	public static String createReleasePointMessage(int pointType,
			String pointName, String userName) {
		String message = null;
		message = "R" + Configuration.messageSeparator + pointName
				+ Configuration.messageSeparator + pointType
				+ Configuration.messageSeparator + userName;
		return message;
	}

}
