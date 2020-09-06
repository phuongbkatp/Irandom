package com.randomappsinc.irandom.importdata;

import androidx.annotation.IntDef;

@IntDef({
        FileImportType.TEXT,
        FileImportType.CSV
})
public @interface FileImportType {
    int TEXT = 0;
    int CSV = 1;
}
