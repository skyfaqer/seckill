package com.cgy.seckill.exception;

import com.cgy.seckill.result.CodeMsg;

public class GlobalException extends RuntimeException {

    private static final long serialVersionUID = 4348798852943105659L;

    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }
}
