package findme.dangdangcrew.global.dto;

import lombok.Builder;

public record ResponseDto<T>(
        T data,
        String msg
) {
    public static <T> ResponseDto<T> of(T data, String msg){
        return new ResponseDto<>(data,msg);
    }
}
