package com.empresa.empresa;

import com.empresa.empresa.repository.EmpleadoRepository;
import com.empresa.empresa.repository.ProyectoRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = ProyectoRepository.class)
@EnableJpaRepositories(basePackageClasses = EmpleadoRepository.class)
@EntityScan(basePackages = "com.empresa.empresa.entity")
public class EmpresaApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmpresaApplication.class, args);
	}

}
