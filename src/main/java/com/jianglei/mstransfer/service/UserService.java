package com.jianglei.mstransfer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jianglei.mstransfer.dao.CommonDao;
import com.jianglei.mstransfer.datasource.TargetDataSource;
import com.jianglei.mstransfer.model.OtherInfo2;
import com.jianglei.mstransfer.model.User;

@Service
public class UserService {

	@Autowired
	private CommonDao dao;

	@TargetDataSource(name="ds1")
	public void test() {
		try {
			User user = dao.selectOne("user.get", 1);
			System.err.println(user);
			List<OtherInfo2> list = user.getList();
			System.out.println(list);
			System.out.println(user.getArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
