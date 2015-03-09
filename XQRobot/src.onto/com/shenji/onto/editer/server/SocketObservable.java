package com.shenji.onto.editer.server;

import java.util.Vector;

import com.shenji.common.log.Log;
import com.shenji.onto.editer.server.SocketServer.Handler;

public class SocketObservable {
	private Vector<SocketObserver> vector;

	public SocketObservable() {
		vector = new Vector<SocketObserver>();
	}

	public synchronized void addObserver(SocketObserver observer) {
		if (observer == null)
			throw new NullPointerException("observer is null!");
		if (!vector.contains(observer)) {
			vector.addElement(observer);
			Log.getLogger(this.getClass()).debug(
					"添加了一个观察者者" + observer.toString());
		}
	}

	public synchronized void deleteObserver(SocketObserver observer) {
		if (observer == null)
			throw new NullPointerException("observer is null!");
		vector.removeElement(observer);
		Log.getLogger(this.getClass()).debug("删除了一个观察者者" + observer.toString());
	}

	public void notifyObservers() {
		notifyObservers(null);
	}

	public void notifyObservers(Object object) {
		synchronized (this) {
			for (SocketObserver observer : vector) {
				observer.update(object);
			}
		}
	}

	/**
	 * @param object
	 * @param userName
	 */
	public void notifyMySelf(Object object, String userName) {
		synchronized (this) {
			for (SocketObserver observer : vector) {
				// 通知自己
				if (((SocketServer.Handler) observer).getClientName().equals(
						userName))
					observer.update(object);
			}
		}
	}

	public void notifyObservers(Object object, String userName) {
		synchronized (this) {
			for (SocketObserver observer : vector) {
				if (!((SocketServer.Handler) observer).getClientName().equals(
						userName))
					observer.update(object);
			}
		}
	}

}
