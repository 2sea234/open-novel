package com.kxhy.novel.mapper;

import com.kxhy.novel.domain.po.OperationLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationMapper {

    @Insert("INSERT INTO operation_log (" +
            "module, type, description, method, request_method, request_url, " +
            "ip, location, params, result, user_id, username, cost_time, " +
            "status, error_msg, create_time" +
            ") VALUES (" +
            "#{module}, #{type}, #{description}, #{method}, #{requestMethod}, #{requestUrl}, " +
            "#{ip}, #{location}, #{params}, #{result}, #{userId}, #{username}, #{costTime}, " +
            "#{status}, #{errorMsg}, #{createTime}" +
            ")")
    void insert(OperationLog operationLog);

}
