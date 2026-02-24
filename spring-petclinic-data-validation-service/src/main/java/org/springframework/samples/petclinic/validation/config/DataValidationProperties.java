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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Data Validation Service, bound from
 * the {@code data-validation} prefix in application.yml.
 */
@ConfigurationProperties(prefix = "data-validation")
public class DataValidationProperties {

	private int maxSampleMismatches = 10;

	private double passThresholdPercent = 100.0;

	private DatasourceProperties sourceDatasource = new DatasourceProperties();

	private DatasourceProperties destinationDatasource = new DatasourceProperties();

	private MainframeFileProperties mainframeFile = new MainframeFileProperties();

	private ApiProperties api = new ApiProperties();

	public int getMaxSampleMismatches() {
		return maxSampleMismatches;
	}

	public void setMaxSampleMismatches(int maxSampleMismatches) {
		this.maxSampleMismatches = maxSampleMismatches;
	}

	public double getPassThresholdPercent() {
		return passThresholdPercent;
	}

	public void setPassThresholdPercent(double passThresholdPercent) {
		this.passThresholdPercent = passThresholdPercent;
	}

	public DatasourceProperties getSourceDatasource() {
		return sourceDatasource;
	}

	public void setSourceDatasource(DatasourceProperties sourceDatasource) {
		this.sourceDatasource = sourceDatasource;
	}

	public DatasourceProperties getDestinationDatasource() {
		return destinationDatasource;
	}

	public void setDestinationDatasource(DatasourceProperties destinationDatasource) {
		this.destinationDatasource = destinationDatasource;
	}

	public MainframeFileProperties getMainframeFile() {
		return mainframeFile;
	}

	public void setMainframeFile(MainframeFileProperties mainframeFile) {
		this.mainframeFile = mainframeFile;
	}

	public ApiProperties getApi() {
		return api;
	}

	public void setApi(ApiProperties api) {
		this.api = api;
	}

	/**
	 * Datasource connection properties for source or destination databases.
	 */
	public static class DatasourceProperties {

		private String url = "jdbc:hsqldb:mem:testdb";

		private String username = "sa";

		private String password = "";

		private String driverClassName = "org.hsqldb.jdbc.JDBCDriver";

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getDriverClassName() {
			return driverClassName;
		}

		public void setDriverClassName(String driverClassName) {
			this.driverClassName = driverClassName;
		}

	}

	/**
	 * Properties for mainframe fixed-width file parsing.
	 */
	public static class MainframeFileProperties {

		private String baseDirectory = "./data/ASCII/";

		private String defaultEncoding = "UTF-8";

		public String getBaseDirectory() {
			return baseDirectory;
		}

		public void setBaseDirectory(String baseDirectory) {
			this.baseDirectory = baseDirectory;
		}

		public String getDefaultEncoding() {
			return defaultEncoding;
		}

		public void setDefaultEncoding(String defaultEncoding) {
			this.defaultEncoding = defaultEncoding;
		}

	}

	/**
	 * Properties for REST API comparison target.
	 */
	public static class ApiProperties {

		private String baseUrl = "http://localhost:8080";

		private int connectTimeoutMs = 5000;

		private int readTimeoutMs = 30000;

		public String getBaseUrl() {
			return baseUrl;
		}

		public void setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
		}

		public int getConnectTimeoutMs() {
			return connectTimeoutMs;
		}

		public void setConnectTimeoutMs(int connectTimeoutMs) {
			this.connectTimeoutMs = connectTimeoutMs;
		}

		public int getReadTimeoutMs() {
			return readTimeoutMs;
		}

		public void setReadTimeoutMs(int readTimeoutMs) {
			this.readTimeoutMs = readTimeoutMs;
		}

	}

}
