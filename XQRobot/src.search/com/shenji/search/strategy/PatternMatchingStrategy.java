package com.shenji.search.strategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shenji.search.inter.IPatternMatchingStrategy;

public class PatternMatchingStrategy {
	public final static int DELETE_=100;  
	public final static int USE_ONLY_ONE_LINE_=101; 
	public final static int BILLNUM_=3101;
	private IPatternMatchingStrategy strategyInter;
	private int strategyType;
	public PatternMatchingStrategy(int strategyType) {
		// TODO Auto-generated constructor stub
		this.strategyType=strategyType;
	}
	
	class deleteRules implements IPatternMatchingStrategy{
		public String getResult(String args, String matcher) {
			// TODO Auto-generated method stub
			//matcher="[\\u4e00-\\u9fa5][:][1-3]\\d{3}-[0-1][1-9]-[0-3]\\d\\s[0-2]\\d[:][0-6]\\d";
			//String pattern=Pattern.compile(matcher); 	
			//if(args.matches(matcher))
				return args.replaceAll(matcher, "");
			//else
			//	return args;
		}
		
	}
	
	class useOnlyOneLineRules implements IPatternMatchingStrategy{
		public String getResult(String args, String matcher) {
			// TODO Auto-generated method stub
			String result=args;
			//if(args.matches(matcher)){
			Pattern pattern=Pattern.compile(matcher); 
				//String[] dataArr=pattern.split(args); 
			Matcher m=pattern.matcher(args);
			while (m.find()) {
				result=m.group(0);
				if(!args.startsWith(result))
					result=args;		
				break;			
			}		
			//}
			return result;	
			/* if(dataArr!=null&&dataArr.length>0)
				 return dataArr[0];
			 else
				 return null;*/
		}
		
	}
	
	class billNum implements IPatternMatchingStrategy{
		public String getResult(String args, String matcher) {
			// TODO Auto-generated method stub
			Pattern pattern=Pattern.compile(matcher); 
			Matcher m=pattern.matcher(args);
			if(m.find()){
				return BILLNUM_+"开头";
			}else{
				return null;
			}
		}
	}
	
	
	public String getResult(String args,String matcher){
		switch (strategyType) {
		case DELETE_:
			strategyInter=this.new deleteRules();
			break;
		case USE_ONLY_ONE_LINE_:
			strategyInter=this.new useOnlyOneLineRules();
			break;
		case BILLNUM_:
			strategyInter = this.new billNum();
			break;
		default:
			break;
		}
		//matcher=matcher.replace("\\", "\\\\");
		return strategyInter.getResult(args, matcher);
		
	}
}
