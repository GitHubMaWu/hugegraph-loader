/*
 * Copyright 2017 HugeGraph Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.baidu.hugegraph.loader.source.file;

import com.baidu.hugegraph.loader.constant.Constants;
import com.baidu.hugegraph.loader.source.AbstractSource;
import com.baidu.hugegraph.loader.source.SourceType;
import com.baidu.hugegraph.loader.util.DateUtil;
import com.baidu.hugegraph.util.E;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"type", "path", "file_filter"})
public class FileSource extends AbstractSource {

    @JsonProperty("path")
    private String path;
    @JsonProperty("file_filter")
    private FileFilter filter;
    @JsonProperty("format")
    private FileFormat format;
    @JsonProperty("delimiter")
    private String delimiter;
    @JsonProperty("date_format")
    private String dateFormat;
    @JsonProperty("time_zone")
    private String timeZone;
    @JsonProperty("skipped_line")
    private SkippedLine skippedLine;
    @JsonProperty("compression")
    private Compression compression;
    @JsonProperty("batch_size")
    private int batchSize;

    public FileSource() {
        this(null, new FileFilter(), FileFormat.CSV, Constants.COMMA_STR,
             Constants.DATE_FORMAT, Constants.TIME_ZONE, new SkippedLine(),
             Compression.NONE, 500);
    }

    @JsonCreator
    public FileSource(@JsonProperty("path") String path,
                      @JsonProperty("filter") FileFilter filter,
                      @JsonProperty("format") FileFormat format,
                      @JsonProperty("delimiter") String delimiter,
                      @JsonProperty("date_format") String dateFormat,
                      @JsonProperty("time_zone") String timeZone,
                      @JsonProperty("skipped_line") SkippedLine skippedLine,
                      @JsonProperty("compression") Compression compression,
                      @JsonProperty("batch_size") Integer batchSize) {
        this.path = path;
        this.filter = filter != null ? filter : new FileFilter();
        this.format = format != null ? format : FileFormat.CSV;
        this.delimiter = delimiter != null ?
                         delimiter : this.format.delimiter();
        this.dateFormat = dateFormat != null ?
                          dateFormat : Constants.DATE_FORMAT;
        this.timeZone = timeZone != null ? timeZone : Constants.TIME_ZONE;
        this.skippedLine = skippedLine != null ?
                           skippedLine : new SkippedLine();
        this.compression = compression != null ? compression : Compression.NONE;
        this.batchSize = batchSize != null ? batchSize : 500;
    }

    @Override
    public SourceType type() {
        return SourceType.FILE;
    }

    @Override
    public void check() throws IllegalArgumentException {
        super.check();
        if (this.format == FileFormat.CSV) {
            E.checkArgument(this.delimiter == null ||
                            this.delimiter.equals(Constants.COMMA_STR),
                            "The delimiter must be '%s' when file format " +
                            "is %s, but got '%s'", Constants.COMMA_STR,
                            this.format, this.delimiter);
        }
        E.checkArgument(DateUtil.checkTimeZone(this.timeZone),
                        "The time_zone '%s' is invalid", this.timeZone);
        if (this.listFormat() != null) {
            String elemDelimiter = this.listFormat().elemDelimiter();
            E.checkArgument(!elemDelimiter.equals(this.delimiter),
                            "The delimiters of fields(%s) and " +
                            "list elements(%s) can't be the same",
                            this.delimiter, elemDelimiter);
        }
    }

    public String path() {
        return this.path;
    }

    public void path(String path) {
        this.path = path;
    }

    public FileFilter filter() {
        return this.filter;
    }

    public void filter(FileFilter filter) {
        this.filter = filter;
    }

    public FileFormat format() {
        return this.format;
    }

    public void format(FileFormat format) {
        this.format = format;
    }

    public String delimiter() {
        return this.delimiter;
    }

    public void delimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String dateFormat() {
        return this.dateFormat;
    }

    public void dateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String timeZone() {
        return this.timeZone;
    }

    public void timeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public SkippedLine skippedLine() {
        return this.skippedLine;
    }

    public void skippedLine(SkippedLine skippedLine) {
        this.skippedLine = skippedLine;
    }

    public Compression compression() {
        return this.compression;
    }

    public void compression(Compression compression) {
        this.compression = compression;
    }

    public int batchSize() {
        return this.batchSize;
    }

    public void batchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public FileSource asFileSource() {
        FileSource source = new FileSource();
        source.header(this.header());
        source.charset(this.charset());
        source.listFormat(this.listFormat());
        source.path = this.path;
        source.filter = this.filter;
        source.format = this.format;
        source.delimiter = this.delimiter;
        source.dateFormat = this.dateFormat;
        source.skippedLine = this.skippedLine;
        source.compression = this.compression;
        return source;
    }

    @Override
    public String toString() {
        return String.format("%s with path %s", this.type(), this.path());
    }
}
