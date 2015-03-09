package com.shenji.search.old;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * ������ʱ���ڳ�ȡ�����ࣨ���š����͵ȣ���ҳ�����ġ�
 * ������<b>�����п�ֲ�����</b>�ķ�����Ϊ����ͨ����û������ض���վ��д����
 * </p>
 * @author  Chen Xin
 * @version 1.0, 2009-11-11
 */
public class TextExtract {
	
	private List<String> lines;
	private int blocksWidth;
	private int threshold;
	private String html;
	private int start;
	private int end;
	private StringBuilder text;
	private ArrayList<Integer> indexDistribution;
	
	{
		lines = new ArrayList<String>();
		indexDistribution = new ArrayList<Integer>();
		text = new StringBuilder();
		blocksWidth = 1;
		/* �����ȡ����ҳ�����������ɿ�����ű���δ�޳�ʱ��ֻҪ�������ֵ���ɡ�*/
		/* ��ֵ����׼ȷ�������ٻ����½���ֵ��С�������󣬵����Ա�֤�鵽ֻ��һ�仰������ */
		threshold	= 16;   
	}
	
	public void setthreshold(int value) {
		threshold = value;
	}

	/**
	 * ��ȡ��ҳ���ģ����жϸ���ҳ�Ƿ���Ŀ¼�͡�����֪����Ŀ϶��ǿ��Գ�ȡ���ĵ���������ҳ��
	 * 
	 * @param _html ��ҳHTML�ַ�
	 * 
	 * @return ��ҳ����string
	 */
	public String parse(String _html) {
		if(_html == null)
			return null;
		html = _html;
		preProcess();
		//System.out.println("p\t\t"+html);
		return getText();
	}
	
	private void preProcess() {
		html = html.replaceAll("(?is)<!DOCTYPE.*?>|(?is)<!--.*?-->", "");
		html = html.replaceAll("(?is)<script.*?>.*?</script>", ""); // remove javascript
		html = html.replaceAll("(?is)<style.*?>.*?</style>", "");   // remove css
		html = html.replaceAll("&.{2,5};|&#.{2,5};", " ");			// remove special char
		html = html.replaceAll("(?is)</?d.*?>|(?is)</?p.*?>|(?is)</?h.*?>|(?is)</?l.*?>|(?is)</?td.*?>", "\n");
		html = html.replaceAll("(?is)<br>", "\n");
		html = html.replaceAll("(?is)<.*?>", "");
		//<!--[if !IE]>|xGv00|9900d21eb16fa4350a3001b3974a9415<![endif]--> 
	}
	
	private boolean withOutPunctuation(String str) {
		Pattern p=Pattern.compile("[����������]");
		Matcher m=p.matcher(str);
		return !m.find();
	}
	
	private String getText() {
		lines = Arrays.asList(html.split("\n"));
		indexDistribution.clear();
		
		for (int i = 0; i < lines.size() - blocksWidth; i++) {
			int wordsNum = 0;
			for (int j = i; j < i + blocksWidth; j++) { 
				lines.set(j, lines.get(j).replaceAll("\\s+", ""));
				wordsNum += lines.get(j).length();
			}
			indexDistribution.add(wordsNum);
			//System.out.println(String.valueOf(i)+":"+lines.get(i));
			//System.out.println(wordsNum);
		}
		for (int i = 0; i < blocksWidth; i++) {
			indexDistribution.add(0);
		}
		
		start = -1; end = -1;
		boolean boolstart = false, boolend = false;
		text.setLength(0);
		int outlines=0;
		for (int i = 0; i < indexDistribution.size() - 3; i++) {
			if (indexDistribution.get(i) > threshold && ! boolstart) {
				if (indexDistribution.get(i+1).intValue() != 0 
					|| indexDistribution.get(i+2).intValue() != 0
					|| indexDistribution.get(i+3).intValue() != 0) {
					boolstart = true;
					start = i;
					continue;
				}
			}
			if (boolstart) {
				if (indexDistribution.get(i).intValue() == 0 
					|| indexDistribution.get(i+1).intValue() == 0) {
					end = i;
					boolend = true;
				}
			}
			StringBuilder tmp = new StringBuilder();
			if (boolend) {
				//System.out.println(start+1 + "\t\t" + end+1);
				for (int ii = start; ii <= end; ii++) {
					if (withOutPunctuation(lines.get(ii))) continue;//�ޱ��
					tmp.append(lines.get(ii) + "\r\n");
				}
				String str = tmp.toString();
				//System.out.println(str);
				//if (str.contains("Copyright")  || str.contains("��Ȩ����") ) continue; 
				text.append(str);
				boolstart = boolend = false;
				outlines++;
				if(outlines>2)
					break;
			}
		}
		return text.toString();
	}
}
