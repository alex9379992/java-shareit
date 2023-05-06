package ru.practicum.shareit.paginator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeptions.PaginationException;


import java.util.List;
@Component
@Slf4j
public class Paginator<T> {

    public List<T> paginationOf(List<T> withoutPages, Long from, Long size) {
        if(from != null & size != null) {
            if(from >= 0 & size > 0 ) {
                int respLastIndex = withoutPages.size();
                if (respLastIndex != 0) {
                    int toIndex = from.intValue() + size.intValue() - 1;
                    if (toIndex > respLastIndex) toIndex = respLastIndex;
                    if (from == toIndex) toIndex++;
                    return withoutPages.subList(from.intValue(), toIndex);
                }
            } else {
                log.warn("Ошибка пагинации");
                throw new PaginationException("Ошибка пагинации");
            }
        }
        return withoutPages;
    }
}