package com.schoolmanagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.schoolmanagement.dao.UserDao;
import com.schoolmanagement.entity.User;
import com.schoolmanagement.utility.Constants.ActiveStatus;
import com.schoolmanagement.utility.Constants.UserRole;

@EnableScheduling
@SpringBootApplication
public class SchoolManagementSystemApplication implements CommandLineRunner {

	private final Logger LOG = LoggerFactory.getLogger(SchoolManagementSystemApplication.class);

	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SchoolManagementSystemApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		User admin = this.userDao.findByEmailIdAndRoleAndStatus("demo.admin@demo.com", UserRole.ROLE_ADMIN.value(),
				ActiveStatus.ACTIVE.value());

		if (admin == null) {

			LOG.info("Admin not found in system, so adding default admin");

			User user = new User();
			user.setEmailId("demo.admin@demo.com");
			user.setPassword(passwordEncoder.encode("123456"));
			user.setRole(UserRole.ROLE_ADMIN.value());
			user.setStatus(ActiveStatus.ACTIVE.value());

			this.userDao.save(user);

		}

	}

}
