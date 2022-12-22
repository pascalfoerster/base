/*
 * Copyright (C) 2022 Sebastian Krieter, Elias Kuiter
 *
 * This file is part of util.
 *
 * util is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 *
 * util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with util. If not, see <https://www.gnu.org/licenses/>.
 *
 * See <https://github.com/FeatureIDE/FeatJAR-util> for further information.
 */
package de.featjar.base.io;

import de.featjar.base.data.IFactory;
import de.featjar.base.data.IFactorySupplier;
import de.featjar.base.data.Maps;
import de.featjar.base.data.Result;
import de.featjar.base.io.format.IFormat;
import de.featjar.base.io.format.IFormatSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

/**
 * Loads inputs and saves outputs of a {@link IFormat}.
 * Loading an {@link AInput} amounts to reading from its source and parsing it using a {@link IFormat}.
 * Saving an {@link AOutput} amounts to and serializing it using a {@link IFormat} and writing to its target.
 *
 * @author Sebastian Krieter
 * @author Elias Kuiter
 */
public class IO {
    /**
     * Default {@link Charset} for loading inputs.
     */
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /**
     * Loads an input.
     *
     * @param inputStream the input stream
     * @param format      the format
     * @param <T>         the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(InputStream inputStream, IFormat<T> format) {
        return load(inputStream, format, DEFAULT_CHARSET);
    }

    /**
     * Loads an input.
     *
     * @param inputStream the input stream
     * @param format      the format
     * @param charset     the charset
     * @param <T>         the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(InputStream inputStream, IFormat<T> format, Charset charset) {
        try (AInputMapper inputMapper = new AInputMapper.Stream(inputStream, charset, null)) {
            return parse(inputMapper, format);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    /**
     * Loads an input.
     *
     * @param url    the URL
     * @param format the format
     * @param <T>    the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(URL url, IFormat<T> format) {
        return load(url, format, DEFAULT_CHARSET);
    }

    /**
     * Loads an input.
     *
     * @param url     the URL
     * @param format  the format
     * @param factory the factory
     * @param <T>     the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(URL url, IFormat<T> format, Supplier<T> factory) {
        return load(url, format, factory, DEFAULT_CHARSET);
    }

    /**
     * Loads an input.
     *
     * @param url            the URL
     * @param formatSupplier the format supplier
     * @param <T>            the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(URL url, IFormatSupplier<T> formatSupplier) {
        return load(url, formatSupplier, DEFAULT_CHARSET);
    }

    /**
     * Loads an input.
     *
     * @param url            the URL
     * @param formatSupplier the format supplier
     * @param factory        the factory
     * @param <T>            the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(URL url, IFormatSupplier<T> formatSupplier, Supplier<T> factory) {
        return load(url, formatSupplier, factory, DEFAULT_CHARSET);
    }

    /**
     * Loads an input.
     *
     * @param url     the URL
     * @param format  the format
     * @param charset the charset
     * @param <T>     the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(URL url, IFormat<T> format, Charset charset) {
        try (AInputMapper inputMapper =
                     new AInputMapper.Stream(url.openStream(), charset, IIOObject.getFileExtension(url.getFile()).orElse(null))) {
            return parse(inputMapper, format);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    /**
     * Loads an input.
     *
     * @param url     the URL
     * @param format  the format
     * @param factory the factory
     * @param charset the charset
     * @param <T>     the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(URL url, IFormat<T> format, Supplier<T> factory, Charset charset) {
        try (AInputMapper inputMapper =
                     new AInputMapper.Stream(url.openStream(), charset, IIOObject.getFileExtension(url.getFile()).orElse(null))) {
            return parse(inputMapper, format, factory);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    /**
     * Loads an input.
     *
     * @param url            the URL
     * @param formatSupplier the format supplier
     * @param factory        the factory
     * @param charset        the charset
     * @param <T>            the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(URL url, IFormatSupplier<T> formatSupplier, Supplier<T> factory, Charset charset) {
        try (AInputMapper inputMapper =
                     new AInputMapper.Stream(url.openStream(), charset, IIOObject.getFileExtension(url.getFile()).orElse(null))) {
            return parse(inputMapper, formatSupplier, factory);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    /**
     * Loads an input.
     *
     * @param url            the URL
     * @param formatSupplier the format supplier
     * @param charset        the charset
     * @param <T>            the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(URL url, IFormatSupplier<T> formatSupplier, Charset charset) {
        try (AInputMapper inputMapper =
                     new AInputMapper.Stream(url.openStream(), charset, IIOObject.getFileExtension(url.getFile()).orElse(null))) {
            return parse(inputMapper, formatSupplier);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    /**
     * Loads an input.
     *
     * @param path            the path
     * @param format          the format
     * @param ioMapperOptions the {@link AIOMapper} options
     * @param <T>             the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(Path path, IFormat<T> format, AIOMapper.Options... ioMapperOptions) {
        return load(path, format, DEFAULT_CHARSET, ioMapperOptions);
    }

    /**
     * Loads an input.
     *
     * @param path            the path
     * @param format          the format
     * @param factory         the factory
     * @param ioMapperOptions the {@link AIOMapper} options
     * @param <T>             the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(Path path, IFormat<T> format, Supplier<T> factory, AIOMapper.Options... ioMapperOptions) {
        return load(path, format, factory, DEFAULT_CHARSET, ioMapperOptions);
    }

    /**
     * Loads an input.
     *
     * @param path            the path
     * @param formatSupplier  the format supplier
     * @param ioMapperOptions the {@link AIOMapper} options
     * @param <T>             the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(Path path, IFormatSupplier<T> formatSupplier, AIOMapper.Options... ioMapperOptions) {
        return load(path, formatSupplier, DEFAULT_CHARSET, ioMapperOptions);
    }

    /**
     * Loads an input.
     *
     * @param path            the path
     * @param formatSupplier  the format supplier
     * @param factory         the factory
     * @param ioMapperOptions the {@link AIOMapper} options
     * @param <T>             the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(Path path, IFormatSupplier<T> formatSupplier, Supplier<T> factory, AIOMapper.Options... ioMapperOptions) {
        return load(path, formatSupplier, factory, DEFAULT_CHARSET, ioMapperOptions);
    }

    /**
     * Loads an input.
     *
     * @param path            the path
     * @param formatSupplier  the format supplier
     * @param factorySupplier the factory supplier
     * @param ioMapperOptions the {@link AIOMapper} options
     * @param <T>             the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(Path path, IFormatSupplier<T> formatSupplier, IFactorySupplier<T> factorySupplier, AIOMapper.Options... ioMapperOptions) {
        return load(path, formatSupplier, factorySupplier, DEFAULT_CHARSET, ioMapperOptions);
    }

    /**
     * Loads an input.
     *
     * @param path            the path
     * @param format          the format
     * @param charset         the charset
     * @param ioMapperOptions the {@link AIOMapper} options
     * @param <T>             the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(Path path, IFormat<T> format, Charset charset, AIOMapper.Options... ioMapperOptions) {
        try (AInputMapper inputMapper = new AInputMapper.File(path, charset, ioMapperOptions)) {
            return parse(inputMapper, format);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    /**
     * Loads an input.
     *
     * @param path            the path
     * @param format          the format
     * @param factory         the factory
     * @param charset         the charset
     * @param ioMapperOptions the {@link AIOMapper} options
     * @param <T>             the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(Path path, IFormat<T> format, Supplier<T> factory, Charset charset, AIOMapper.Options... ioMapperOptions) {
        try (AInputMapper inputMapper = new AInputMapper.File(path, charset, ioMapperOptions)) {
            return parse(inputMapper, format, factory);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    /**
     * Loads an input.
     *
     * @param path            the path
     * @param formatSupplier  the format supplier
     * @param factory         the factory
     * @param charset         the charset
     * @param ioMapperOptions the {@link AIOMapper} options
     * @param <T>             the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(Path path, IFormatSupplier<T> formatSupplier, Supplier<T> factory, Charset charset, AIOMapper.Options... ioMapperOptions) {
        try (AInputMapper inputMapper = new AInputMapper.File(path, charset, ioMapperOptions)) {
            return parse(inputMapper, formatSupplier, factory);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    /**
     * Loads an input.
     *
     * @param path            the path
     * @param formatSupplier  the format supplier
     * @param charset         the charset
     * @param ioMapperOptions the {@link AIOMapper} options
     * @param <T>             the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(Path path, IFormatSupplier<T> formatSupplier, Charset charset, AIOMapper.Options... ioMapperOptions) {
        try (AInputMapper inputMapper = new AInputMapper.File(path, charset, ioMapperOptions)) {
            return parse(inputMapper, formatSupplier);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    /**
     * Loads an input.
     *
     * @param path            the path
     * @param formatSupplier  the format supplier
     * @param factorySupplier the factory supplier
     * @param charset         the charset
     * @param ioMapperOptions the {@link AIOMapper} options
     * @param <T>             the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(Path path, IFormatSupplier<T> formatSupplier, IFactorySupplier<T> factorySupplier, Charset charset, AIOMapper.Options... ioMapperOptions) {
        try (AInputMapper inputMapper = new AInputMapper.File(path, charset, ioMapperOptions)) {
            return parse(path, inputMapper, formatSupplier, factorySupplier);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    /**
     * Loads an input.
     *
     * @param string the string
     * @param format the format
     * @param <T>    the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(String string, IFormat<T> format) {
        try (AInputMapper inputMapper =
                     new AInputMapper.String(string, DEFAULT_CHARSET, null)) {
            return parse(inputMapper, format);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    /**
     * Loads an input.
     *
     * @param string  the string
     * @param format  the format
     * @param factory the factory
     * @param <T>     the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(String string, IFormat<T> format, IFactory<T> factory) {
        try (AInputMapper inputMapper =
                     new AInputMapper.String(string, DEFAULT_CHARSET, null)) {
            return parse(inputMapper, format, factory);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    /**
     * Loads an input.
     *
     * @param string         the string
     * @param path           the path
     * @param formatSupplier the format supplier
     * @param factory        the factory
     * @param <T>            the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(String string, Path path, IFormatSupplier<T> formatSupplier, IFactory<T> factory) {
        try (AInputMapper inputMapper =
                     new AInputMapper.String(string, DEFAULT_CHARSET, IIOObject.getFileExtension(path).orElse(null))) {
            return parse(inputMapper, formatSupplier, factory);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    /**
     * Loads an input.
     *
     * @param string         the string
     * @param path           the path
     * @param formatSupplier the format supplier
     * @param <T>            the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(String string, Path path, IFormatSupplier<T> formatSupplier) {
        try (AInputMapper inputMapper =
                     new AInputMapper.String(string, DEFAULT_CHARSET, IIOObject.getFileExtension(path).orElse(null))) {
            return parse(inputMapper, formatSupplier);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    /**
     * Loads an input.
     *
     * @param string          the string
     * @param path            the path
     * @param formatSupplier  the format supplier
     * @param factorySupplier the factory supplier
     * @param <T>             the type of the parsed result
     * @return the parsed result
     */
    public static <T> Result<T> load(
            String string, Path path, IFormatSupplier<T> formatSupplier, IFactorySupplier<T> factorySupplier) {
        try (AInputMapper inputMapper =
                     new AInputMapper.String(string, DEFAULT_CHARSET, IIOObject.getFileExtension(path).orElse(null))) {
            return parse(path, inputMapper, formatSupplier, factorySupplier);
        } catch (final IOException e) {
            return Result.empty(e);
        }
    }

    /**
     * Parses an input.
     *
     * @param inputMapper the input mapper
     * @param format      the format
     * @param factory     the factory
     * @param <T>         the type of the parsed result
     * @return the parsed result
     */
    private static <T> Result<T> parse(AInputMapper inputMapper, IFormat<T> format, Supplier<T> factory) {
        return format.supportsParse()
                ? format.getInstance().parse(inputMapper, factory)
                : Result.empty(new UnsupportedOperationException(format.toString()));
    }

    /**
     * Parses an input.
     *
     * @param inputMapper the input mapper
     * @param format      the format
     * @param <T>         the type of the parsed result
     * @return the parsed result
     */
    private static <T> Result<T> parse(AInputMapper inputMapper, IFormat<T> format) {
        return format.supportsParse()
                ? format.getInstance().parse(inputMapper)
                : Result.empty(new UnsupportedOperationException(format.toString()));
    }

    /**
     * Parses an input.
     *
     * @param inputMapper    the input mapper
     * @param formatSupplier the format supplier
     * @param factory        the factory
     * @param <T>            the type of the parsed result
     * @return the parsed result
     */
    private static <T> Result<T> parse(AInputMapper inputMapper, IFormatSupplier<T> formatSupplier, Supplier<T> factory) {
        return inputMapper
                .get()
                .getInputHeader()
                .flatMap(formatSupplier::getFormat)
                .flatMap(format -> parse(inputMapper, format, factory));
    }

    /**
     * Parses an input.
     *
     * @param inputMapper    the input mapper
     * @param formatSupplier the format supplier
     * @param <T>            the type of the parsed result
     * @return the parsed result
     */
    private static <T> Result<T> parse(AInputMapper inputMapper, IFormatSupplier<T> formatSupplier) {
        return inputMapper
                .get()
                .getInputHeader()
                .flatMap(formatSupplier::getFormat)
                .flatMap(format -> parse(inputMapper, format));
    }

    /**
     * Parses an input.
     *
     * @param inputMapper     the input mapper
     * @param path            the path
     * @param formatSupplier  the format supplier
     * @param factorySupplier the factory supplier
     * @param <T>             the type of the parsed result
     * @return the parsed result
     */
    private static <T> Result<T> parse(Path path, AInputMapper inputMapper, IFormatSupplier<T> formatSupplier, IFactorySupplier<T> factorySupplier) {
        return inputMapper
                .get()
                .getInputHeader()
                .flatMap(formatSupplier::getFormat)
                .flatMap(format -> factorySupplier
                        .getFactory(path, format)
                        .flatMap(factory -> parse(inputMapper, format, factory)));
    }

    /**
     * Saves an output.
     *
     * @param object          the object
     * @param path            the path
     * @param format          the format
     * @param ioMapperOptions the {@link AIOMapper} options
     * @param <T>             the type of the object
     */
    public static <T> void save(T object, Path path, IFormat<T> format, AIOMapper.Options... ioMapperOptions)
            throws IOException {
        save(object, path, format, DEFAULT_CHARSET, ioMapperOptions);
    }

    /**
     * Saves an output.
     *
     * @param object          the object
     * @param path            the path
     * @param format          the format
     * @param charset         the charset
     * @param ioMapperOptions the {@link AIOMapper} options
     * @param <T>             the type of the object
     */
    public static <T> void save(
            T object, Path path, IFormat<T> format, Charset charset, AIOMapper.Options... ioMapperOptions)
            throws IOException {
        if (format.supportsSerialize()) {
            try (AOutputMapper outputMapper = AOutputMapper.of(path, charset, ioMapperOptions)) {
                format.getInstance().write(object, outputMapper);
            }
        }
    }

    /**
     * Saves an output.
     *
     * @param object    the object
     * @param format    the format
     * @param outStream the output stream
     * @param <T>       the type of the object
     */
    public static <T> void save(T object, OutputStream outStream, IFormat<T> format) throws IOException {
        save(object, outStream, format, DEFAULT_CHARSET);
    }

    /**
     * Saves an output.
     *
     * @param object    the object
     * @param format    the format
     * @param outStream the output stream
     * @param charset   the charset
     * @param <T>       the type of the object
     */
    public static <T> void save(T object, OutputStream outStream, IFormat<T> format, Charset charset)
            throws IOException {
        if (format.supportsSerialize()) {
            try (AOutputMapper outputMapper = new AOutputMapper.Stream(outStream, charset)) {
                format.getInstance().write(object, outputMapper);
            }
        }
    }

    /**
     * {@return the object printed as a string}
     *
     * @param object the object
     * @param format the format
     * @param <T>    the type of the object
     */
    public static <T> String print(T object, IFormat<T> format) throws IOException {
        if (format.supportsSerialize()) {
            try (AOutputMapper outputMapper = new AOutputMapper.String(DEFAULT_CHARSET)) {
                format.getInstance().write(object, outputMapper);
                return outputMapper.get().getOutputStream().toString();
            }
        }
        return "";
    }

    /**
     * {@return the object hierarchy printed as a collection of strings}
     *
     * @param object the object
     * @param format the format
     * @param <T>    the type of the object
     */
    public static <T> LinkedHashMap<Path, String> printHierarchy(T object, IFormat<T> format) throws IOException {
        if (format.supportsSerialize()) {
            try (AOutputMapper.String outputMapper = new AOutputMapper.String(DEFAULT_CHARSET)) {
                format.getInstance().write(object, outputMapper);
                return outputMapper.getOutputStrings();
            }
        }
        return Maps.empty();
    }

    /**
     * Writes a string into a physical file.
     *
     * @param string  the string
     * @param path    the path
     * @param charset the charset
     * @throws IOException if an I/O error occurs
     */
    public static void write(String string, Path path, Charset charset) throws IOException {
        Files.write(
                path,
                string.getBytes(charset),
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE);
    }

    /**
     * Writes a string into a physical file.
     *
     * @param string the string
     * @param path   the path
     * @throws IOException if an I/O error occurs
     */
    public static void write(String string, Path path) throws IOException {
        write(string, path, DEFAULT_CHARSET);
    }
}
