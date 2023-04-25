package com.dai.music.service;

import com.dai.music.domain.RankList;

public interface RankListService {

  int rankOfSongListId(Long songListId);

  boolean addRank(RankList rankList);
}
