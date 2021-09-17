package com.alipay.sofa.doc.service;

import com.alipay.sofa.doc.model.Context;
import com.alipay.sofa.doc.model.Repo;
import com.alipay.sofa.doc.model.TOC;

/**
 *
 */
public interface TOCParser {

    TOC parse(Repo repo, Context context);
}
