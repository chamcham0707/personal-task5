package com.sparta.ottoon.follow.filter;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FollowerSearchCond {
    private String authorName;

    public FollowerSearchCond(String authorName) {
        this.authorName = authorName;
    }
}
