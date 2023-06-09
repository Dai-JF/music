package com.dai.music.dao;

import com.dai.music.domain.RankList;
import org.springframework.stereotype.Repository;

@Repository
public interface RankListMapper {

  int insert(RankList record);

  int insertSelective(RankList record);

  /**
   * 查总分
   *
   * @param songListId
   * @return
   */
  int selectScoreSum(Long songListId);

  /**
   * 查总评分人数
   *
   * @param songListId
   * @return
   */
  int selectRankNum(Long songListId);

}
