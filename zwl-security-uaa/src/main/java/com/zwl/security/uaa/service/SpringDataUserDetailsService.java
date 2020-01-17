package com.zwl.security.uaa.service;

import com.alibaba.fastjson.JSON;
import com.zwl.security.uaa.dao.UserDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @ClassNameSpringDataUserDetailsService
 * @Description
 * @Author 二师兄
 * @Date2020-01-14 15:17
 * @Version V1.0
 **/
@Service
public class SpringDataUserDetailsService implements UserDetailsService {
//    @Autowired
//    UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //登录账号
        //根据账号去数据库查询...
        //为了方便测试，伪代码一个用户
        System.out.println("username=" + username);
        UserDto user = UserDto.builder().id("1").fullname("二师兄").username("admin").password("$2a$10$Y9J4K8xZfSgMHuRkgyGf6.opzQ3ifIPNAzY6zwY2sZitE8OWYOYX6").build();
        String userJson = JSON.toJSONString(user);
        UserDetails userDetails = User.withUsername(userJson).password(user.getPassword()).authorities("p1").build();
        return userDetails;

//        //连接数据库根据账号查询用户信息
//        UserDto userDto = userDao.getUserByUsername(username);
//        if(userDto == null){
//            //如果用户查不到，返回null，由provider来抛出异常
//            return null;
//        }
//        //根据用户的id查询用户的权限
//        List<String> permissions = userDao.findPermissionsByUserId(userDto.getId());
//        //将permissions转成数组
//        String[] permissionArray = new String[permissions.size()];
//        permissions.toArray(permissionArray);
//        //将userDto转成json
//        String principal = JSON.toJSONString(userDto);
//        UserDetails userDetails = User.withUsername(principal).password(userDto.getPassword()).authorities(permissionArray).build();
//        return userDetails;
    }
}