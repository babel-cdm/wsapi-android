Web Service API
_________________________________________________________________________________

Este api puede ejecutar peticiones del tipo:

_________________________________________________________________________________

	- PUT
	- POST
	- GET
	- DELETE

_________________________________________________________________________________

Los parámetro que recibe la petición son:

_________________________________________________________________________________

	- TYPE: Wsapi.Type (enumerado con el tipo de petición)
	- ID: un string que se asignara como identificador para cuando se reciba la respuesta
	- URL: la url a la cual se quiere hacer la petición
	- HEADER: la cabecera que se quiere enviar. En formato <valor, clave> utilizando un Map de Java
	- BODY: el cuerpo de la petición en formato String
	- LISTENER: la clase que implementa la interfaz donde se recibiran los resultados

_________________________________________________________________________________

Para hacer la petición, utilizando WsApiManager debemos obtener el WSApi y pasarle un objeto del tipo RequestParams
que sea el que contenga los parámetros de la petición.

Todos los parámetros son opcionales a excepción de TYPE y URL. En caso de no incluirlos en la llamada, 
se producirá una excepción y el resultado de la misma se devolverá en el método "onError" de la interfaz (acompañado del id).

Una llamada de ejemplo sería la siguiente:

_________________________________________________________________________________

	Map<String, String> map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");
        map.put("clave", "valor");

        String json = "{\n" +
                " \"value\":\"15\"\n" +
                "}";

        WSApiManager.getWSApi().setParams(new RequestParams()
                .setType(WSApi.Type.POST)
                .setId("ws.id.prueba")
                .setUrl("http://www.google.es")
                .setHeader(map)
                .setBody(json)
                .setListener(this)).execute();

__________________________________________________________________________________


Si el resultado de la ejecución es correcto, tendremos la respuesta en el método onSuccess de la interfaz, acompañada del
id de la llamada y de la cabecera

_________________________________________________________________________________