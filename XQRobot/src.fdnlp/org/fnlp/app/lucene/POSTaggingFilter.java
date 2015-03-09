package org.fnlp.app.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;

import com.shenji.nlp.structure.MChineseTags;


import edu.fudan.nlp.cn.Tags;

public final class POSTaggingFilter extends FilteringTokenFilter {

  
  private final POSAttribute posAtt = addAttribute(POSAttribute.class);


  public POSTaggingFilter(boolean enablePositionIncrements, TokenStream in) {
    super(enablePositionIncrements, in);
  }
  
  @Override
  public boolean accept() throws IOException {
    String pos = posAtt.getPartOfSpeech();
    //【修改源码】采用自己的词性分析类
    //return !Tags.isStopword(pos);
    return !MChineseTags.isStopword(pos);
  }
}