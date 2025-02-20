package com.clokey.server.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

@Configuration
public class ElasticsearchConfig {


    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUri;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        String[] uriParts = elasticsearchUri.replace("http://", "").split(":");
        String host = uriParts[0];
        int port = Integer.parseInt(uriParts[1]);

        RestClient restClient = RestClient.builder(
                new HttpHost(host, port, "http")
        ).build();

        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
        );

        return new ElasticsearchClient(transport);
    }
}
