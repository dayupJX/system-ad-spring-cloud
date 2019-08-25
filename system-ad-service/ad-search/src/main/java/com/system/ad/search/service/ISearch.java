package com.system.ad.search.service;

import com.system.ad.search.vo.SearchRequest;
import com.system.ad.search.vo.SearchResponse;

public interface ISearch {

    SearchResponse findAds(SearchRequest request);
}
