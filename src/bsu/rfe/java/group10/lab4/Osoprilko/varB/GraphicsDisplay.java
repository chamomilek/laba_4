package bsu.rfe.java.group10.lab4.Osoprilko.varB;


import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;

public class GraphicsDisplay extends JPanel {
    // Список координат точек для построения графика
    private Double[][] graphicsData;
    private Double [][] graphicsData2;
    // Флаговые переменные, задающие правила отображения графика
    private boolean showAxis = true;
    private boolean showMarkers = true;
    // Границы диапазона пространства, подлежащего отображению
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    // Используемый масштаб отображения
    private double scale;
    // Различные стили черчения линий
    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    // Шрифт отображения подписей к осям координат
    private Font axisFont;

    public GraphicsDisplay() {
// Цвет заднего фона области отображения - белый
        setBackground(Color.WHITE);
// Сконструировать необходимые объекты, используемые в рисовании
// Перо для рисования графика
        graphicsStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 10.0f, null, 0.0f);
// Перо для рисования осей координат
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
// Перо для рисования контуров маркеров
        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
// Шрифт для подписей осей координат
        axisFont = new Font("Serif", Font.BOLD, 36);
    }

//--------------------------------------------------------------------------------------

    // Метод вызывается из обработчика элемента меню "Открыть файл с графиком"
// главного окна приложения в случае успешной загрузки данных
    public void showGraphics(Double[][] graphicsData) {
// Сохранить массив точек во внутреннем поле класса
        this.graphicsData = graphicsData;
// Запросить перерисовку компонента (неявно вызвать paintComponent())
        repaint();
    }
    public void showGraphics2(Double[][] graphicsData2)
    {
        // Сохранить массив точек во внутреннем поле класса
        this.graphicsData2 = graphicsData2;
        // Запросить перерисовку компонента, т.е. неявно вызвать paintComponent()
        repaint();
    }

//---------------------------------------------------------------------------------------
// Методы-модификаторы для изменения параметров отображения графика
// Изменение любого параметра приводит к перерисовке области
    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }
    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }
    protected Point2D.Double xyToPoint(double x, double y) {
// Вычисляем смещение X от самой левой точки (minX)
        double deltaX = x - minX;
// Вычисляем смещение Y от точки верхней точки (maxY)
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX*scale, deltaY*scale);
    }
    protected Point2D.Double shiftPoint(Point2D.Double src,
                                        double deltaX, double deltaY) {
// Инициализировать новый экземпляр точки
        Point2D.Double dest = new Point2D.Double();
// Задать еѐ координаты как координаты существующей точки +
// заданные смещения
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }

    //--------------------------------------------------------------------------


    protected void paintGraphics(Graphics2D canvas) {
// Выбрать линию для рисования графика
        canvas.setStroke(graphicsStroke);
// Выбрать цвет линии
        canvas.setColor(Color.RED);
/* Будем рисовать линию графика как путь, состоящий из множества
сегментов (GeneralPath). Начало пути устанавливается в первую точку
графика, после чего прямой соединяется со следующими точками */
        GeneralPath graphics = new GeneralPath();
        for (int i=0; i<graphicsData.length; i++) {
// Преобразовать значения (x,y) в точку на экране point
            Point2D.Double point = xyToPoint(graphicsData[i][0],
                    graphicsData[i][1]);
            if (i>0) {
// Не первая итерация – вести линию в точку point
                graphics.lineTo(point.getX(), point.getY());
            } else {
// Первая итерация - установить начало пути в точку point
                graphics.moveTo(point.getX(), point.getY());
            }
        }
// Отобразить график
        canvas.draw(graphics);
        if(twoGraphics)
        {
            canvas.setStroke(graphicsStroke2);
            // Выбрать цвет линии
            canvas.setColor(Color.MAGENTA);
            GeneralPath graphics2 = new GeneralPath();
            for (int i=0; i<graphicsData2.length; i++)
            {
                // Преобразовать значения (x,y) в точку на экране point
                Point2D.Double point = xyToPoint(graphicsData2[i][0], graphicsData2[i][1]);
                if (i>0) {
                    // Не первая итерация цикла - вести линию в точку point
                    graphics2.lineTo(point.getX(), point.getY());
                }
                else
                {
                    // Первая итерация цикла - установить начало пути в точку point
                    graphics2.moveTo(point.getX(), point.getY());
                }
            }
            // Отобразить график
            canvas.draw(graphics2);
        }
    }
    protected void paintAxis(Graphics2D canvas) {
// Шаг 1 – установить необходимые настройки рисования
// Установить особое начертание для осей
        canvas.setStroke(axisStroke);
// Оси рисуются чѐрным цветом
        canvas.setColor(Color.BLACK);
// Стрелки заливаются чѐрным цветом
        canvas.setPaint(Color.BLACK);
// Подписи к координатным осям делаются специальным шрифтом
        canvas.setFont(axisFont);
// Создать объект контекста отображения текста - для получения
// характеристик устройства (экрана)
        FontRenderContext context = canvas.getFontRenderContext();
// Шаг 2 - Определить, должна ли быть видна ось Y на графике
        if (minX <= 0.0 && maxX >= 0.0) {
// Она видна, если левая граница показываемой области minX<=0.0,
// а правая (maxX) >= 0.0
// Шаг 2а - ось Y - это линия между точками (0, maxY) и (0, minY)
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
// Шаг 2б - Стрелка оси Y
            GeneralPath arrow = new GeneralPath();
// Установить начальную точку ломаной точно на верхний конец оси Y
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
// Вести левый "скат" стрелки в точку с относительными
// координатами (5,20)
            arrow.lineTo(arrow.getCurrentPoint().getX()+5,
                    arrow.getCurrentPoint().getY()+20);
// Вести нижнюю часть стрелки в точку с относительными
// координатами (-10, 0)
            arrow.lineTo(arrow.getCurrentPoint().getX()-10,
                    arrow.getCurrentPoint().getY());
// Замкнуть треугольник стрелки
            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow); // Закрасить стрелку
// Шаг 2в - Нарисовать подпись к оси Y
// Определить, сколько места понадобится для надписи “y”
            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
// Вывести надпись в точке с вычисленными координатами
            canvas.drawString("y", (float)labelPos.getX() + 10,
                    (float)(labelPos.getY() - bounds.getY()));
        }
        // Шаг 3 - Определить, должна ли быть видна ось X на графике
        if (minY<=0.0 && maxY>=0.0) {
// Она видна, если верхняя граница показываемой области max)>=0.0,
// а нижняя (minY) <= 0.0
// Шаг 3а - ось X - это линия между точками (minX, 0) и (maxX, 0)
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0),
                    xyToPoint(maxX, 0)));
// Шаг 3б - Стрелка оси X
            GeneralPath arrow = new GeneralPath();
// Установить начальную точку ломаной точно на правый конец оси X
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
// Вести верхний "скат" стрелки в точку с относительными
// координатами (-20,-5)
            arrow.lineTo(arrow.getCurrentPoint().getX()-20,
                    arrow.getCurrentPoint().getY()-5);
// Вести левую часть стрелки в точку
// с относительными координатами (0, 10)
            arrow.lineTo(arrow.getCurrentPoint().getX(),
                    arrow.getCurrentPoint().getY()+10);
// Замкнуть треугольник стрелки
            arrow.closePath();
            canvas.draw(arrow); // Нарисовать стрелку
            canvas.fill(arrow); // Закрасить стрелку
// Шаг 3в - Нарисовать подпись к оси X
// Определить, сколько места понадобится для надписи “x”
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
// Вывести надпись в точке с вычисленными координатами
            canvas.drawString("x",
                    (float)(labelPos.getX()-bounds.getWidth()-10),
                    (float)(labelPos.getY() + bounds.getY()));
        }
    }

    //------------------------------------------------------------------------------

    protected void paintMarkers(Graphics2D canvas) {
// Шаг 1 - Установить специальное перо для черчения контуров маркеров
        canvas.setStroke(markerStroke);
// Выбрать красный цвета для контуров маркеров
        canvas.setColor(Color.RED);
// Выбрать красный цвет для закрашивания маркеров внутри
        canvas.setPaint(Color.RED);
// Шаг 2 - Организовать цикл по всем точкам графика
        for (Double[] point: graphicsData) {
// Инициализировать эллипс как объект для представления маркера
            Ellipse2D.Double marker = new Ellipse2D.Double();
/* Эллипс будет задаваться посредством указания координат его
центра и угла прямоугольника, в который он вписан */
// Центр - в точке (x,y)
            Point2D.Double center = xyToPoint(point[0], point[1]);
// Угол прямоугольника - отстоит на расстоянии (3,3)
            Point2D.Double corner = shiftPoint(center, 3, 3);
// Задать эллипс по центру и диагонали
            marker.setFrameFromCenter(center, corner);
            canvas.draw(marker); // Начертить контур маркера
            canvas.fill(marker); // Залить внутреннюю область маркера
        }
    }
}