package xyz.cofe.stsl.conf.text;

import java.util.ArrayList;

public class Text {
    /**
     * Делит текст на строки согласно символам перевода строк: CR+LF/CR/LF (\r\n, \r, \n).<br> Сами символы (CR,LF) не
     * входят в результирующий набор строк.
     *
     * @param line Текст
     * @return Результат.
     */
    public static String[] splitNewLines( String line ){
        if( line == null ){
            throw new IllegalArgumentException("line==null");
        }
        int idx = 0;
        int len = line.length();
        char c1;
        char c2;
        StringBuilder buff = new StringBuilder();
        ArrayList<String> lines = new ArrayList<>();
        while( idx < len ) {
            c1 = line.charAt(idx);
            c2 = idx < (len - 1) ? line.charAt(idx + 1) : (char) 0;

            //CR+LF - Windows,Dos
            if( c1 == '\r' && c2 == '\n' ){
                lines.add(buff.toString());
                buff.setLength(0);
                idx += 2;
                continue;
            }

            // Acorn BBC, RISC OS
            if( c1 == '\n' && c2 == '\r' ){
                lines.add(buff.toString());
                buff.setLength(0);
                idx += 2;
                continue;
            }

            // Mac os
            if( c1 == '\r' && c2 != '\n' ){
                lines.add(buff.toString());
                buff.setLength(0);
                idx += 1;
                continue;
            }

            // Unix, linux, ....
            if( c1 == '\n' && c2 != '\r' ){
                lines.add(buff.toString());
                buff.setLength(0);
                idx += 1;
                continue;
            }

            buff.append(c1);
            idx++;
        }
        lines.add(buff.toString());
        return lines.toArray(new String[]{});
    }

    /**
     * Добавляет отступ
     *
     * @param sourceLines Исходный набор строк
     * @param indent      Отступ
     * @return Строки с отступом
     */
    public static String[] indent( String indent, String[] sourceLines ){
        if( sourceLines == null ){
            throw new IllegalArgumentException("sourceLines == null");
        }
        if( indent == null ){
            throw new IllegalArgumentException("indent == null");
        }

        String[] res = new String[sourceLines.length];
        for( int i = 0; i < sourceLines.length; i++ ){
            res[i] = indent + sourceLines[i];
        }
        return res;
    }

    /**
     * Добавляет отступ в начале каждой строки
     *
     * @param indent  отступ
     * @param srcText текст
     * @return текст с отступом
     */
    public static String indent( String indent, String srcText ){
        if( srcText == null ) throw new IllegalArgumentException("srcText==null");
        if( indent == null ) throw new IllegalArgumentException("indent==null");

        String[] lines = splitNewLines(srcText);
        for( int i = 0; i < lines.length; i++ ){
            lines[i] = indent + lines[i];
        }
        return join(lines, System.lineSeparator());
    }

    /**
     * Добавляет отступ в начало каждой строки
     *
     * @param indent    Отступ
     * @param source    Исходный текст
     * @param lineDelim Разделитель строк
     * @return Строки с отступом
     */
    public static String indent( String indent, String source, String lineDelim ){
        if( source == null ){
            throw new IllegalArgumentException("source == null");
        }
//        if (lineDelim == null) {
//            throw new IllegalArgumentException("lineDelim == null");
//        }
        if( lineDelim == null ) lineDelim = System.lineSeparator();
        if( indent == null ){
            throw new IllegalArgumentException("indent == null");
        }
        String[] sourceLines = splitNewLines(source);
        String[] res = new String[sourceLines.length];
        for( int i = 0; i < sourceLines.length; i++ ){
            res[i] = indent + sourceLines[i];
        }
        return join(res, lineDelim);
    }

    /**
     * Объединяет строки вставляя между ними заданную строку
     *
     * @param lines Строки
     * @param glue  Вставка
     * @return Результат склейки
     */
    public static String join( String[] lines, String glue ){
        if( lines == null ){
            throw new IllegalArgumentException("lines == null");
        }
        if( glue == null ){
            throw new IllegalArgumentException("glue == null");
        }
        return join(lines, glue, 0, lines.length);
    }

    /**
     * Объединяет строки вставляя между ними заданную строку
     *
     * @param lines Строки
     * @param glue  Вставка
     * @param from  С какой строки начать
     * @param count Сколько строк объединять
     * @return Результат склейки
     */
    public static String join( String[] lines, String glue, int from, int count ){
        if( lines == null ){
            throw new IllegalArgumentException("lines == null");
        }
        if( glue == null ){
            throw new IllegalArgumentException("glue == null");
        }
        return join(lines, glue, from, count, false);
    }

    /**
     * Объединяет строки вставляя между ними заданную строку
     *
     * @param lines     Строки
     * @param glue      Вставка
     * @param from      С какой строки начать
     * @param count     Сколько строк объединять
     * @param withNulls включить также пустые ссылки
     * @return Результат склейки
     */
    public static String join( String[] lines, String glue, int from, int count, boolean withNulls ){
        if( lines == null ){
            throw new IllegalArgumentException("lines == null");
        }
        if( glue == null ){
            throw new IllegalArgumentException("glue == null");
        }
        if( count == 0 ) return "";
        int includedLinesCount = 0;

        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < count; i++ ){
            int idx = from + i;

            boolean rightOutside = idx >= lines.length;
            if( rightOutside ) break;

            boolean leftOutside = idx < 0;
            if( leftOutside ) continue;

            String line = lines[idx];
            if( !withNulls && line == null ) continue;

            if( includedLinesCount > 0 ){
                sb.append(glue);
            }

            sb.append(line);
            includedLinesCount++;
        }
        return sb.toString();
    }
}
