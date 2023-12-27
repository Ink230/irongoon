package lod.irongoon.parse.external;

import java.util.List;

public interface DataParser {
    List<String[]> load(String filePath);
}
