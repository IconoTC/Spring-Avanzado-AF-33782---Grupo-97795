package com.example.aop;

import java.util.Optional;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
public class DummyService {
    private String value = null;
    @Lazy @Autowired 
    DummyService self;

	public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }

    public void setValue(@NonNull String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("No acepto argumentos nulos");
        }
        this.value = value;
    }
    
    public void clearValue() {
//    	value = null;
    	value = alwaysNull();
//    	value = self.alwaysNull();
//      value = ((DummyService) AopContext.currentProxy()).alwaysNull(); // auto referenciado
    }

    @NonNull 
    public String echo(String input) {
        return input;
    }
    
    public String alwaysNull() {
        return null;
    }

}
