package com.shenji.onto.reasoner.data;

import java.util.Map;

import com.shenji.common.log.Log;
import com.shenji.onto.mapping.FaqMapUtil;

public class PruningServer {
	public boolean firstLevelPurning(ReasonerRoute route,
			ReasonerRoute otherRoute,
			Map<ReasonerRoute, Integer> distCardRouteMap) {
		boolean routeFirstLevel = ((UserReasonerNodeData) route.get(1)
				.getNodeData()).isAbstract();
		boolean otherRouteFirstLevel = ((UserReasonerNodeData) otherRoute
				.get(1).getNodeData()).isAbstract();
		if (!routeFirstLevel && otherRouteFirstLevel
				|| (routeFirstLevel && !otherRouteFirstLevel)) {
			// 整条路径不要了
			if (!routeFirstLevel) {
				distCardRouteMap.put(otherRoute, 0);
			} else if (!otherRouteFirstLevel) {
				distCardRouteMap.put(route, 0);
			}
			return false;
		}
		return true;
	}

	public boolean virtualPurning(ReasonerRoute route,
			Map<ReasonerRoute, Integer> distCardRouteMap) {
		if(route==null||route.size()<=1)
			return true;
		String str="";
		int count=0;
		for(ReasonerNode node:route.getRouteList()){
			UserReasonerNodeData data=(UserReasonerNodeData) node.getNodeData();
			if(!data.isAbstract()&&!str.contains(data.getNodeName())){
				count++;
			}
			str=str+data.getNodeName();
		}
		if(count==0){
			//整条不要了
			distCardRouteMap.put(route, 0);
			return false;
		}
		else
			return true;

	}

	public boolean ordinaryPurning(ReasonerRoute route,
			ReasonerRoute otherRoute,
			Map<ReasonerRoute, Integer> distCardRouteMap) {
		int pruningLocationNum = route.isBrotherPruning(otherRoute);
		/*if (pruningLocationNum == -1) {
			// 第一层词出现要却确保
			//return true;
		} else {*/
			try {
				ReasonerRoute discardRoute = ReasonerRoute.doPrunning(route,
						otherRoute);
				if (discardRoute == null)
					return true;
				distCardRouteMap.put(discardRoute, pruningLocationNum);
			} catch (Exception e) {
				// 类型转换异常不剪枝了
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			}
			return false;
		//}
	}

}
