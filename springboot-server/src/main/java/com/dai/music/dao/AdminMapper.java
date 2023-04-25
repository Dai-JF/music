package com.dai.music.dao;

import com.dai.music.domain.Admin;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminMapper {
  int deleteByPrimaryKey(Integer id);

  int insert(Admin record);

  int insertSelective(Admin record);

  Admin selectByPrimaryKey(Integer id);

  int updateByPrimaryKeySelective(Admin record);

  int updateByPrimaryKey(Admin record);

  int verifyPassword(String username, String password);
}
