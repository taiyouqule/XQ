package com.shenji.onto;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLIndividual;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultOWLObjectProperty;

/**
 * 本体常量
 * @author zhq
 */
public interface OntologyCommon {
	/**
	 * 该词为其他词定义的同义词
	 */
	public final static String IsOthersSynonyms="IsOthersSynonyms";
	/**
	 * 本体一切元素的根元素名
	 */
	public final static String OntoRootName="Thing";
	public final static String OntoOwlRootName="owl:Thing";
	/**
	 * 本体概念
	 * @author zhq
	 */
	public class DataType{
		/**
		 * 命名类
		 */
		public static final int namedClass=11;
		/**
		 * 数据属性（静态）
		 */
		public static final int dataTypeProperty=12;
		/**
		 * 对象属性（动态）
		 */
		public static final int objectProperty=13;
		/**
		 * 实例
		 */
		public static final int individual=14; 
		
		/**
		 * 通过本体概念类型获得 本体类型字符串
		 * @param ontologyType 本体概念类型
		 * @return 本体类型字符串
		 */
		public static String getDataTypeStr(int ontologyType){
			if(ontologyType==namedClass)
				return "NamedClass";
			if(ontologyType==dataTypeProperty)
				return "DataTypeProperty";
			if(ontologyType==objectProperty)
				return "ObjectProperty";
			if(ontologyType==individual)
				return "Individual";
			return null;	
		}
		 /**
		  * 通过本体资源获得 本体类型字符串
		 * @param resource 本体资源
		 * @return 本体类型字符串
		 */
		public static String getDataTypeStr(RDFResource resource){
        	 if(resource instanceof DefaultOWLNamedClass)
        		 return "NamedClass";
        	 else if(resource instanceof DefaultOWLObjectProperty)
        		 return "ObjectProperty";
        	 else if(resource instanceof DefaultOWLDatatypeProperty)
        		 return "DataTypeProperty";
        	 else if(resource instanceof DefaultOWLIndividual)
        		 return "Individual";
        	 return null;
	     }
     
	}
	 /**
	  * 本体复合概念
	 * @author zhq
	 */
	public class ComplexDataType {
         /**
         * 对象属性与实例（喝哈尔滨啤酒--喝（动作，对象属性），哈尔滨啤酒（实例，啤酒命名类的实例））
         */
        public final static int objectProperty_individual = 71;
         /**
         * 数据属性与值（年龄12--年龄（静态数据属性），12（数值））
         */
        public final static int dataProperty_value = 72;
         /**
         * 对象属性与命名类（喝啤酒--喝（动作，对象属性），啤酒（命名类））
         */
        public final static int objectProperty_namedClass = 73;
         /**
         * 数据属性与实例（作者莫言--作者（静态数据属性），莫言（实例，人命名类的实例））
         */
        public final static int dataProperty_cardinality = 74;
         /**
          * 通过本体复合概念得到 本体复合概念字符串
         * @param type 本体复合概念
         * @return 本体复合概念字符串
         */
        public static String getComplexDataTypeStr(int type){
        	 switch (type) {
				case objectProperty_individual:
					return "ObjectProperty_Individual";
				case dataProperty_value:
					return "DataProperty_Value";
				case objectProperty_namedClass:
					return "ObjectProperty_NamedClass";
				case dataProperty_cardinality:
					return "DataProperty_Cardinality";
				default:
					break;
				}
			return null;
	     }
     }
	
	/**
	 * 本体概念间描述
	 * @author zhq
	 */
	public class Description{
		/**
		 * 概念间相等
		 */
		public static final int equivalent_=21;
		//
		/**
		 * 父概念
		 */
		public static final int super_=22;
		/**
		 * 相反概念
		 */
		public static final int inverse_=23;
		/**
		 * 不相交概念（如男人女人）
		 */
		public static final int disjoint_=24;
		/**
		 * 子概念
		 */
		public static final int sub_=25;
		/**
		 * 匿名推断父概念（男人--人--动物，则动物是男人匿名推断父概念）
		 */
		public static final int inferred_super=26;
		/**
		 * 匿名推断子概念（男人--人--动物，则男人是动物匿名推断子概念）
		 */
		public static final int inferred_sub=27;
		
		/**
		 * 通过本体概念间描述 获得 本体概念间描述字符串
		 * @param descriptionType
		 * @return
		 */
		public static String getDescriptionStr(int descriptionType){
			if(descriptionType==equivalent_)
				return "Equivalent";
			if(descriptionType==super_)
				return "Super";
			if(descriptionType==inverse_)
				return "Inverse";
			if(descriptionType==disjoint_)
				return "Disjoint";
			if(descriptionType==sub_)
				return "Sub";
			if(descriptionType==inferred_super)
				return "Inferred_super";
			if(descriptionType==inferred_sub)
				return "Inferred_sub";
			return null;
			
		}
	}
	
	/**
	 * 本体属性特性
	 * @author zhq
	 * 属性为：对象属性或数据属性
	 */
	public class Characteristics{
		/**
		 * 特征枚举
		 * @author zhq
		 */
		public static enum CharacteristicsType{
			/**
			 * 函数性
			 */
			functional(31),
			/**
			 * 反函数性
			 */
			inverser_fuction(32),
			/**
			 * 传递性
			 */
			transitvie(33),
			/**
			 * 对称性
			 */
			symmetric(34),
			/**
			 * 不对称性
			 */
			asymmetric(35),
			/**
			 * 反射性
			 */
			reflexive(36),
			/**
			 * 非反射性
			 */
			irreflexive(37);
			private int num;
			private CharacteristicsType(int num){
				this.num=num;
			}
			public int getNum(){
				return num;
			}
		}
		/**
		 * 得到属性支持的特征
		 * @return
		 */
		public static String[] getCharacteristics(){
			List<String> list=null;
			try{
				list=new ArrayList<String>();
				for(CharacteristicsType chType:CharacteristicsType.values()){
					list.add(chType.name());
				}
				return (String[])list.toArray(new String[list.size()]);	
			}
			finally{
				if(list!=null)
					list.clear();
			}
		}
		
	}
	
	
	
	/**
	 * 本体属性限制类型
	 * @author zhq
	 * 属性：对象属性或数据属性
	 */
	public class ProPertyLimitType{
		/**
		 * 值域
		 */
		public static final int range=51;
		/**
		 * 定义域
		 */
		public static final int domain=52;
		/**
		 * 通过属性限制类型得到 属性限制类型字符串
		 * @param descriptionType
		 * @return
		 */
		public static String getLimitStr(int limit){
			if(limit==range)
				return "Range";
			if(limit==domain)
				return "Domain";
			return null;
		}
	}
	
	/**
	 * RDF数据类型
	 * @author zhq
	 * 本体数据属性支持的数据类型
	 */
	public class RDFSDataType{
		public enum DataType{
			string_("string"),int_("int"),float_("float"),
			double_("double"),boolean_("boolean"),long_("long"),
			date_("date"),datetime_("datetime"),byte_("byte");
			private String name;
			private DataType(String name){
				this.name=name;
			}
			public String getName(){
				return name;
			}
		}
		/**
		 * 通过JAVA基本数据类型 得到RDF数据类型字符串
		 * @param obj JAVA基本数据类型
		 * @return RDF数据类型字符串
		 */
		public static String getBasicDataType(Object obj){
			if(obj instanceof String)
				return DataType.string_.getName();
			else if(obj instanceof Integer)
				return DataType.int_.getName();
			else if(obj instanceof Float)
				return DataType.float_.getName();
			else if(obj instanceof Double)
				return DataType.double_.getName();
			else if(obj instanceof Boolean)
				return DataType.boolean_.getName();
			else if(obj instanceof Long)
				return DataType.long_.getName();
			else if(obj instanceof RDFSDatatype)
				return DataType.date_.getName();
			else if(obj instanceof RDFSDatatype)
				return DataType.datetime_.getName();
			else if(obj instanceof Byte)
				return DataType.byte_.getName();
			return null;
			  
		}
		
		/**
		 * 通过RDF数据字符串 获得 RDF数据对象
		 * @param owlModel 本体模型
		 * @param rdfDataStr RDF数据字符串
		 * @return RDF数据模型
		 */
		public static RDFSDatatype getRDFSDatatype(JenaOWLModel owlModel,String rdfDataStr){
			if(rdfDataStr==null)
				return null;
			if(rdfDataStr.equals(DataType.string_.getName()))
				return owlModel.getXSDstring();
			if(rdfDataStr.equals(DataType.int_.getName()))
				return owlModel.getXSDint();
			if(rdfDataStr.equals(DataType.float_.getName()))
				return owlModel.getXSDfloat();
			if(rdfDataStr.equals(DataType.double_.getName()))
				return owlModel.getXSDdouble();
			if(rdfDataStr.equals(DataType.boolean_.getName()))
				return owlModel.getXSDboolean();
			if(rdfDataStr.equals(DataType.long_.getName()))
				return owlModel.getXSDlong();
			if(rdfDataStr.equals(DataType.date_.getName()))
				return owlModel.getXSDdate();
			if(rdfDataStr.equals(DataType.datetime_.getName()))
				return owlModel.getXSDdateTime();
			if(rdfDataStr.equals(DataType.byte_.getName()))
				return owlModel.getXSDbyte();
			return null;		
		}	
		/**
		 * 得到支持的RDF数据类型字符串 集合
		 * @return
		 */
		public static String[] getRDFDataType(){
			List<String> list=null;
			try{
				list=new ArrayList<String>();
				for(OntologyCommon.RDFSDataType.DataType dateType:OntologyCommon.RDFSDataType.DataType.values()){
					list.add(dateType.getName());
				}
				return (String[])list.toArray(new String[list.size()]);	
			}
			finally{
				if(list!=null)
					list.clear();
			}
		}
	
		
	}
	
	/**
	 * 本体概念数量修饰
	 * @author zhq
	 * eg:拥有四个轮子的车=汽车（拥有（对象属性），车（命名类），四个轮子（exectly=4））这是命名类与复合概念的等价
	 */
	public class Cardinality{		
		/**
		 * 只有一个
		 */
		public static final int only=61;
		/**
		 * 有一些（非确定）
		 */
		public static final int some=62;
		/**
		 * 最小有
		 */
		public static final int min=63;
		/**
		 * 最大有
		 */
		public static final int max=64;
		/**
		 * 准确有
		 */
		public static final int exactly=65;
		/**
		 * 通过本体概念数量修饰类型 得到本体概念属性修饰字符串
		 * @param type
		 * @return
		 */
		public static String getCardinalityStr(int type){
			if(type==only)
				return "only";
			if(type==some)
				return "some";
			if(type==max)
				return "max";
			if(type==min)
				return "min";
			if(type==exactly)
				return "exactly";
			return null;
			
		}
	}
	
	/**
	 * 本体概念声明
	 * @author zhq
	 */
	public class Annotations{
		//versionInfo去除。留给被其他词定义为同义词专用(又启用)
		//RDF系统定义：注释和标签
		public enum AnnotationType{comment,label}
		/**
		 * 得到支持的RDF概念声明集合
		 * @return string[]
		 */
		public static String[] getAnnotations(){
			List<String> list=null;
			try{
				list=new ArrayList<String>();
				for(AnnotationType anType:AnnotationType.values()){
					list.add(anType.name());
				}
				return (String[])list.toArray(new String[list.size()]);	
			}
			finally{
				if(list!=null)
					list.clear();
			}
		}
		
	
	}

	
}
