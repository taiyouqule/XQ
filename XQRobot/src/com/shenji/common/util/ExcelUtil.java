package com.shenji.common.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.shenji.common.log.Log;
import com.shenji.wordclassification.FAQDataAnalysis;
import com.shenji.wordclassification.WordPrepertyBean;

public class ExcelUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<WordPrepertyBean> list = new FAQDataAnalysis()
				.queryTable("speechtagging");
		wirte(list);
	}

	public static ArrayList<String> readFileColumn(String filePath,
			int sheet_num, int column, int start, int end) {
		File file = new File(filePath);
		ArrayList<String> arrayList = new ArrayList<String>();
		Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(file);
			// 得到第一个工作表
			Sheet sheet = workbook.getSheet(sheet_num);
			Cell cell = null;
			String result = null;
			if (end == -1) {
				end = sheet.getRows();
				// System.out.println(end);
			}
			for (int i = start; i < end; i++) {
				// sheet.
				cell = sheet.getCell(column, i);
				result = cell.getContents();
				// System.out.println(result);
				arrayList.add(result);
			}
			// System.out.println(arrayList.size());
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			Log.getLogger(ExcelUtil.class).error(e.getMessage(),e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(ExcelUtil.class).error(e.getMessage(),e);
		} finally {
			if (workbook != null)
				workbook.close();
		}
		return arrayList;
	}

	public static int getLineNum(String filePath, int sheet_num) {
		File file = new File(filePath);
		// ArrayList<String> arrayList=new ArrayList<String>();
		Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(file);
			// 得到第一个工作表
			Sheet sheet = workbook.getSheet(sheet_num);
			return sheet.getRows();
			// System.out.println(end);
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			Log.getLogger(ExcelUtil.class).error(e.getMessage(),e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(ExcelUtil.class).error(e.getMessage(),e);
		}
		// System.out.println(arrayList.size());
		finally {
			if (workbook != null)
				workbook.close();
		}
		return -1;
	}

	public static ArrayList<String> readFileLine(String filePath,
			int sheet_num, int line, int start, int end) {
		File file = new File(filePath);
		ArrayList<String> arrayList = new ArrayList<String>();
		Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(file);
			// 得到第一个工作表
			Sheet sheet = workbook.getSheet(sheet_num);
			Cell cell = null;
			String result = null;
			if (end == -1) {
				end = sheet.getColumns();
			}
			for (int i = start; i < end; i++) {
				cell = sheet.getCell(i, line);
				result = cell.getContents();
				arrayList.add(result);
			}
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			Log.getLogger(ExcelUtil.class).error(e.getMessage(),e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.getLogger(ExcelUtil.class).error(e.getMessage(),e);
		} finally {
			if (workbook != null)
				workbook.close();
		}
		return arrayList;
	}

	public static void wirte(List<WordPrepertyBean> list) {
		try {
			// 打开文件
			WritableWorkbook book = Workbook.createWorkbook(new File(
					"D:/test123.xls"));
			// 生成名为“第一页”的工作表，参数0表示这是第一页
			WritableSheet sheet = book.createSheet(" 第一页 ", 0);
			String[] lableStr = { "word", "en", "ch", "source", "count" };
			for (int i = 0; i < 5; i++) {
				Label label = new Label(i, 0, lableStr[i]);
				sheet.addCell(label);
			}
			Collections.sort(list, new Comparator<WordPrepertyBean>() {
				@Override
				public int compare(WordPrepertyBean o1, WordPrepertyBean o2) {
					// TODO Auto-generated method stub
					return o2.getCount() - o1.getCount();
				}
			});
			// 将定义好的单元格添加到工作表中
			for (int i = 0; i < list.size(); i++) {
				for (int j = 0; j < 5; j++) {
					String str = null;
					if (j == 0)
						str = list.get(i).getWord();
					else if (j == 1)
						str = list.get(i).getSpeech_en();
					else if (j == 2)
						str = list.get(i).getSpeech_ch();
					else if (j == 3)
						str = list.get(i).getSource();
					else if (j == 4)
						str = String.valueOf(list.get(i).getCount());
					// System.out.println(str);
					Label label = new Label(j, i + 1, str);
					sheet.addCell(label);
				}
			}
			// 写入数据并关闭文件
			book.write();
			book.close();
		} catch (Exception e) {
			Log.getLogger(ExcelUtil.class).error(e.getMessage(),e);
		}
	}

}
