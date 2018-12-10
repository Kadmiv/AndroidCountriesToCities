Какие были проблемные моменты:
- Кривой Json. Я нашел идентичный, но более красивый - https://raw.githubusercontent.com/meMo-Minsk/all-countries-and-cities-json/master/countries.json переделал на нормальный вид и залил в репо с проектом. Надеюсь, это не будет проблемой!
- There's a problem: Your input was larger than 51200 characters, try making this a bit smaller - http://www.jsonschema2pojo.org :) Не хотелось возиться с POJO классом, да и вдруг появиться еще какая страна - парсер универсальнее на данный файл)
- Не спарсилось несколько стран - Mexico, Cocos [Keeling] Islands, Myanmar [Burma]

Почему такие библиотеки:
- Retrofit 2 - думаю обьяснять не стоит .)
- Reaml - с ней работается быстрее, так и работает она быстрее.
нужно получить country alpha-code 2