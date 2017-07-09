package com.jianglei.mstransfer;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.jianglei.mstransfer.dao.CommonDao;
import com.jianglei.mstransfer.model.OtherInfo2;
import com.jianglei.mstransfer.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MsTransferApplicationTests {
	
	@Autowired
	private CommonDao dao;
	
	@Test
	public void test1() {
		try {
			
			User user = dao.selectOne("user.get", 1);
			List<OtherInfo2> list = user.getList();
			System.out.println(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
