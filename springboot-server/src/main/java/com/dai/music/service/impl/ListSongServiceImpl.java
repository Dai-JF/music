package com.dai.music.service.impl;

import com.dai.music.dao.ListSongMapper;
import com.dai.music.domain.ListSong;
import com.dai.music.service.ListSongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListSongServiceImpl implements ListSongService {

  @Autowired
  private ListSongMapper listSongMapper;

  @Override
  public List<ListSong> allListSong() {
    return listSongMapper.allListSong();
  }

  @Override
  public boolean updateListSongMsg(ListSong listSong) {
    return listSongMapper.updateListSongMsg(listSong) > 0;
  }

  @Override
  public boolean deleteListSong(Integer songId) {
    return listSongMapper.deleteListSong(songId) > 0;
  }

  @Override
  public boolean addListSong(ListSong listSong) {
    return listSongMapper.insertSelective(listSong) > 0;
  }

  @Override
  public List<ListSong> listSongOfSongId(Integer songListId) {
    return listSongMapper.listSongOfSongId(songListId);
  }

}
