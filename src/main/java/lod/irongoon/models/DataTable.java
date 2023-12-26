package lod.irongoon.models;

import java.util.List;
import java.util.stream.Collectors;

public class DataTable {
    public List<String[]> data;

    public DataTable(List<String[]> data) {
        this.data = data.stream().map(String[]::clone).collect(Collectors.toList());
    }
}
