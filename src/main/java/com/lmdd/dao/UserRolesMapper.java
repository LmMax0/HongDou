package com.lmdd.dao;

import com.lmdd.pojo.login.UserRolesKey;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserRolesMapper {
    int deleteByPrimaryKey(UserRolesKey key);

    int insert(UserRolesKey record);

    int insertSelective(UserRolesKey record);
}