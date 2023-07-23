package io.flomesh.demo.model;

import lombok.*;
import lombok.experimental.PackagePrivate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Book {

    @PackagePrivate
    String id;
    @PackagePrivate
    int value;
}
