Web Service API
_________________________________________________________________________________

Este api puede ejecutar peticiones del tipo:

_________________________________________________________________________________

	- PUT
	- POST
	- GET
	- DELETE

_________________________________________________________________________________

Los par�metro que recibe la petici�n son:

_________________________________________________________________________________

	- TYPE: Wsapi.Type (enumerado con el tipo de petici�n)
	- ID: un string que se asignara como identificador para cuando se reciba la respuesta
	- URL: la url a la cual se quiere hacer la petici�n
	- HEADER: la cabecera que se quiere enviar. En formato <valor, clave> utilizando un Map de Java
	- BODY: el cuerpo de la petici�n en formato String
	- LISTENER: la clase que implementa la interfaz donde se recibiran los resultados

_________________________________________________________________________________

Para hacer la petici�n, utilizando WsApiManager debemos obtener el WSApi y pasarle un objeto del tipo RequestParams
que sea el que contenga los par�metros de la petici�n.

Todos los par�metros son opcionales a excepci�n de TYPE y URL. En caso de no incluirlos en la llamada, 
se producir� una excepci�n y el resultado de la misma se devolver� en el m�todo "onError" de la interfaz (acompa�ado del id).

Una llamada de ejemplo ser�a la siguiente:

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


Si el resultado de la ejecuci�n es correcto, tendremos la respuesta en el m�todo onSuccess de la interfaz, acompa�ada del
id de la llamada y de la cabecera

_________________________________________________________________________________