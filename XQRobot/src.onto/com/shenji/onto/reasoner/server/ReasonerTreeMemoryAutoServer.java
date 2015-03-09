package com.shenji.onto.reasoner.server;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.shenji.common.log.Log;
import com.shenji.onto.mapping.FaqMapServices;
import com.shenji.onto.mapping.JsonFaqHtml;
import com.shenji.onto.reasoner.ReasonerLabel;
import com.shenji.onto.reasoner.data.FaqReasonerNodeData;
import com.shenji.onto.reasoner.data.ReasonerNode;
import com.shenji.onto.reasoner.data.ReasonerRoute;
import com.shenji.onto.reasoner.data.ReasonerTree;
import com.shenji.search.FenciControl;
import com.shenji.search.ResourcesControl;


public class ReasonerTreeMemoryAutoServer extends ReasonerTreeAbsServer {
	private ReasonerTree faqOntoTree = null;
	private ExecutorService exec = null;
	private int treeCount = 0;
	private ResourcesControl resourcesControl;
	private FenciControl fenciControl;

	public ReasonerTree getFaqOntoTree() {
		return this.faqOntoTree;
	}

	public synchronized void updateTreeCount() {
		this.treeCount++;
	}

	public synchronized int getTreeCount() {
		return this.treeCount;
	}

	public ReasonerTreeMemoryAutoServer() throws RuntimeException {
		super();
		this.fenciControl=new FenciControl();
		this.resourcesControl=new ResourcesControl();
		exec = Executors.newCachedThreadPool();
		faqOntoTree = ReasonerTreeOperate.getBigFaqReasonerTree(true)[0];
		//这里应该用变量控制，加载不同策略
		//this.initFaqsReasonerTrees_Single(0);
		this.initFaqsReasonerTrees();
	}

	/*
	 * public int getMapSize() { return this.treeMap.size(); }
	 */
	public void close() {
		this.clear();
		if (this.exec != null)
			this.exec.shutdown();
	}


	public void clear() {
		if (this.faqOntoTree != null){
			this.faqOntoTree.clear();
			this.faqOntoTree=null;
		}
	}

	public synchronized void rebuildFaqsReasonerTrees() throws Exception {
		this.clear();
		faqOntoTree = ReasonerTreeOperate.getBigFaqReasonerTree(true)[0];
		initFaqsReasonerTrees();
	}

	public class InitFaqTreeTread extends Thread {
		private ReasonerTreeAbsServer server;
		private List<String[]> list;

		public InitFaqTreeTread(ReasonerTreeAbsServer server,
				List<String[]> list) {
			this.server = server;
			this.list = list;
		}

		public void run() {
			try {

				for (String[] strs : list) {
					String faqId = FaqMapServices
							.analysisHrefTogetName(strs[0]);
					if (faqId == null)
						continue;
					//System.out.println(strs[1]);
					//FAQWebServices webServices = new FAQWebServices();
					//用带权重的分词
					String[] words = fenciControl.iKAnalysisWithPossibility(strs[1]);
					String[] expandWords = ReasonerLabel.getInstance()
							.wordsLabelExpand(words, false,true);
					words = null;
					ReasonerTree[] trees = ReasonerTreeOperate
							.getUserReasonerTree(expandWords, false,true);
					ReasonerTree tree = null;
					//if (trees != null && trees.length > 0)
						tree = trees[0];
					this.server.addFaqTree(tree, faqId);
					((ReasonerTreeMemoryAutoServer)this.server).updateTreeCount();
					
					int count=((ReasonerTreeMemoryAutoServer)this.server).getTreeCount();
					if(count%100==0)
					{
						System.out.println("");
					}
					else						
						System.out.print(((ReasonerTreeMemoryAutoServer)this.server).getTreeCount() + " ");
				}
			} catch (Exception e) {
				// TODO: handle exception
				Log.getLogger(this.getClass()).error(e.getMessage(),e);
			} finally {
				if (list != null)
					list.clear();
			}
		}
	}

	private void initFaqsReasonerTrees_Single(int maxCount) throws Exception {
		if(maxCount==0)
			return;
		List<String[]> urlList = JsonFaqHtml
				.jsoupAllFAQHtml_UrlAndQ(resourcesControl.listAllFaq());
		int division = 100;
		// int threadNum = urlList.size() / division + 1;
		ReasonerLabel.getInstance().inintLabelMap();
		// 更新一下本体词库(这里有问题，只更新knowledge)
		ReasonerServer.getInstance().getOntoWords(
				ReasonerServer.getInstance().getOnlyOntMode(), true);
		int count = 0;
		for (String[] strs : urlList) {
			if (count++ == maxCount)
				break;

			String faqId = FaqMapServices.analysisHrefTogetName(strs[0]);
			if (faqId == null)
				continue;
			// faqId = StaticCommon.FaqMappingCommon.INDIVIDUAL_HEAD + faqId;
			//用带权重的分词
			String[] words = fenciControl.iKAnalysisWithPossibility(strs[1]);
			String[] expandWords = ReasonerLabel.getInstance()
					.wordsLabelExpand(words, false,true);
			words = null;
			
			ReasonerTree[] trees = ReasonerTreeOperate.getUserReasonerTree(
					expandWords, false,true);
			ReasonerTree tree = null;
			if (trees != null && trees.length > 0)
				tree = trees[0];
			this.addFaqTree(tree, faqId);
			this.updateTreeCount();
			if (tree != null)
				tree.clear();
			System.out.println(this.getTreeCount() + " ");
		}
		if (urlList != null)
			urlList.clear();
		
	}


	public Set<String> filter(ReasonerTree tree) {
		Set<String> reSet = new LinkedHashSet<String>();
		List<ReasonerRoute> routes = tree.getReasonerRoutes();
		List<ReasonerNode> reasonerNodes = new ArrayList<ReasonerNode>();
		for (ReasonerRoute route : routes) {
			ReasonerNode faqOntoTreeNode = this.faqOntoTree.find(route, route
					.get(route.size() - 1).getNodeData().getNodeName(),
					route.size() - 1);
			if (faqOntoTreeNode != null) {
				reasonerNodes.add(faqOntoTreeNode);
				faqOntoTreeNode.getAllChilds(reasonerNodes,
						faqOntoTreeNode.getFirstChilds());
				for (ReasonerNode node : reasonerNodes) {
					Set<String> faqIdSet = ((FaqReasonerNodeData) node
							.getNodeData()).getFaqIdList();
					reSet.addAll(faqIdSet);
					//Log.debugSystemOut(node.getNodeData().getNodeName());

				}
				reasonerNodes.clear();
			}
			route.clear();
		}
		if (routes != null) {
			routes.clear();
		}
		if (reasonerNodes != null)
			reasonerNodes.clear();
		return reSet;
	}

	private void initFaqsReasonerTrees() throws RuntimeException {
		// treeMap = new ResultMap<ReasonerTree, String>(initialCapacity);
		List<String[]> urlList = JsonFaqHtml
				.jsoupAllFAQHtml_UrlAndQ(resourcesControl.listAllFaq());
		int division = 100;
		int threadNum = urlList.size() / division + 1;
		ReasonerLabel.getInstance().inintLabelMap();
		// 更新一下本体词库(这里有问题，只更新knowledge)
		ReasonerServer.getInstance().getOntoWords(
				ReasonerServer.getInstance().getOnlyOntMode(), true);
		for (int i = 0; i < threadNum; i++) {
			int start = i * division;
			int end = (i + 1) * division;
			if (i == threadNum - 1)
				end = urlList.size();
			List<String[]> list = new ArrayList<String[]>(urlList.subList(
					start, end));
			Thread thread = new InitFaqTreeTread(this, list);
			this.exec.execute(thread);
		}
		//this.exec.shutdown();
	
	}

	

	public synchronized void deleteFaqTree(Object... obj) {
		if (!(obj[0] instanceof ReasonerTree)) {
			return;
		}
		if (!(obj[1] instanceof String)) {
			return;
		}
		ReasonerTree tree = (ReasonerTree) obj[0];
		String faqId = obj[1].toString();

	}


	public synchronized void addFaqTree(Object... obj) {
		if (!(obj[0] instanceof ReasonerTree)) {
			return;
		}
		if (!(obj[1] instanceof String)) {
			return;
		}
		ReasonerTree tree = (ReasonerTree) obj[0];
		String faqId = obj[1].toString();
		List<ReasonerRoute> routes = tree.getReasonerRoutes();
		for (ReasonerRoute route : routes) {
			ReasonerNode faqOntoTreeNode = this.faqOntoTree.find(route, route
					.get(route.size() - 1).getNodeData().getNodeName(),
					route.size() - 1);
			if (faqOntoTreeNode != null){
				((FaqReasonerNodeData) faqOntoTreeNode.getNodeData())
						.addFaqId(faqId);
				//Log.promptMsg(faqOntoTreeNode.getNodeData().getNodeName()+"#"+faqId);
			}
			route.clear();
		}
		if (routes != null) {
			routes.clear();
		}
	}

	public synchronized void updateFaqTree(Object... obj) {
		if (!(obj[0] instanceof ReasonerTree)) {
			return;
		}
		if (!(obj[1] instanceof String)) {
			return;
		}
		ReasonerTree tree = (ReasonerTree) obj[0];
		String faqId = obj[1].toString();
	}

	@Override
	public ReasonerTree[] getFaqReasonerTree(String faqId) {
		// TODO Auto-generated method stub
		/*
		 * if (this.map.containsKey(individualName)){ ReasonerTree[]
		 * trees=this.map.get(individualName);
		 * //System.out.println("[输出内存树模型]"+trees[0].toString()); return trees;
		 * } else return null;
		 */
		return null;
	}

	@Override
	public ReasonerTree[] getUserReasonerTree(String[] words) {
		// TODO Auto-generated method stub
		boolean isWithPossiblity=true;
		String[] expandWords = ReasonerLabel.getInstance().wordsLabelExpand(
				words, false,isWithPossiblity);
		words = null;	
		return ReasonerTreeOperate.getUserReasonerTree(expandWords, true,isWithPossiblity);
	}

	

}
