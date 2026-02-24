/*
 * Copyright 2002-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.validation.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Configuration for source and destination DataSources used in database comparisons.
 * Each DataSource is built from the YAML-driven {@link DataValidationProperties}.
 */
@Configuration
@EnableConfigurationProperties(DataValidationProperties.class)
public class DataSourceConfig {

	@Bean(name = "sourceDataSource")
	public DataSource sourceDataSource(DataValidationProperties properties) {
		DataValidationProperties.DatasourceProperties ds = properties.getSourceDatasource();
		return DataSourceBuilder.create()
			.url(ds.getUrl())
			.username(ds.getUsername())
			.password(ds.getPassword())
			.driverClassName(ds.getDriverClassName())
			.build();
	}

	@Bean(name = "destinationDataSource")
	public DataSource destinationDataSource(DataValidationProperties properties) {
		DataValidationProperties.DatasourceProperties ds = properties.getDestinationDatasource();
		return DataSourceBuilder.create()
			.url(ds.getUrl())
			.username(ds.getUsername())
			.password(ds.getPassword())
			.driverClassName(ds.getDriverClassName())
			.build();
	}

	@Bean(name = "sourceJdbcTemplate")
	public JdbcTemplate sourceJdbcTemplate(DataSource sourceDataSource) {
		return new JdbcTemplate(sourceDataSource);
	}

	@Bean(name = "destinationJdbcTemplate")
	public JdbcTemplate destinationJdbcTemplate(DataSource destinationDataSource) {
		return new JdbcTemplate(destinationDataSource);
	}

}
