# Домашнее задание № 02
Движение игровых объектов по полю.

## Цель:
Выработка навыка применения SOLID принципов на примере игры "Космическая битва".
В результате выполнения ДЗ будет получен код, отвечающий за движение объектов по игровому полю, 
устойчивый к появлению новых игровых объектов и дополнительных ограничений, накладываемых на это движение.

## Описание/Пошаговая инструкция выполнения домашнего задания:

### Описание задания:
Реализовать движение объектов на игровом поле в рамках подсистемы Игровой сервер.
- Прямолинейное равномерное движение без деформации. 
  - Само движение реализовано в виде отдельного класса. 
  - Для движущихся объектов определен интерфейс, устойчивый к появлению новых видов движущихся объектов 
  - Тесты:
    - Для объекта, находящегося в точке (12, 5) и движущегося со скоростью (-7, 3), движение меняет положение объекта на (5, 8)
    - Попытка сдвинуть объект, у которого невозможно прочитать положение в пространстве, приводит к ошибке
    - Попытка сдвинуть объект, у которого невозможно прочитать значение мгновенной скорости, приводит к ошибке
    - Попытка сдвинуть объект, у которого невозможно изменить положение в пространстве, приводит к ошибке
- Вращательное движение вокруг собственной оси.
  - Сам поворот реализован в виде отдельного класса
  - Для поворачивающегося объекта определен интерфейс, устойчивый к появлению новых видов движущихся объектов
  - Тесты