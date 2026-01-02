package com.eagledev.bookreaders.mappers.config;

import com.eagledev.bookreaders.mappers.AuthorMapper;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
@ComponentScan(basePackageClasses = {UserDetailsManager.class, AuthorMapper.class})
public class MapperConfig {
}
