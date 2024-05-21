package br.com.vulpicula.aws_project02.controller;


import br.com.vulpicula.aws_project02.model.ProductEventLog;
import br.com.vulpicula.aws_project02.model.ProductEventLogDto;
import br.com.vulpicula.aws_project02.repository.ProducEventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api")
public class ProductEventLogController {

    @Autowired
    ProducEventLogRepository producEventLogRepository;


    @GetMapping("/events")
    public List<ProductEventLogDto> getAllEvents(){
        return StreamSupport
                .stream(producEventLogRepository.findAll().spliterator(),false)
                .map(ProductEventLogDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/events{code}")
    public List <ProductEventLogDto> finByCode(@PathVariable String code) {
        return producEventLogRepository.findAllByPk(code)
                .stream()
                .map(ProductEventLogDto::new)
                .collect((Collectors.toList()));
    }

    @GetMapping("/events/{code}/{event}")
    public List<ProductEventLogDto> findByCodeAndEventType(@PathVariable String code,
                                                           @PathVariable String event) {
        return producEventLogRepository.findAllByPkAndSkStartsWith(code, event)
                .stream()
                .map(ProductEventLogDto::new)
                .collect(Collectors.toList());
    }
}
