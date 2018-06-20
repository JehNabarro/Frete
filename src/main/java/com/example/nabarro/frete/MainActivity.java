package com.example.nabarro.frete;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    EditText cep;
    TextView preco;
    TextView prazo;
    Button calc;
    Button comprar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cep = (EditText) findViewById(R.id.editText);
        preco = (TextView) findViewById(R.id.textView);
        prazo = (TextView) findViewById(R.id.textView2);
        calc = (Button) findViewById(R.id.button);
        comprar = (Button) findViewById(R.id.Comprar);


        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // por ser uma classe tem que ser criado o objeto,
                new XmlAsyncTask().execute(cep.getText().toString());


            }
        });

        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, Main2Activity.class);
                i.putExtra("the_value", 900);
                startActivity(i);

            }
        });

    }

    public static Document loadXMLFromString(String xml)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        InputSource is = new InputSource(new StringReader(xml));
        try {
            return builder.parse(is);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    class XmlAsyncTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        // Metodo responsavel por fazer a requisição

        @Override
        protected String doInBackground(String... urls) {
            try {

                //------------------>>
                HttpGet httppost = new HttpGet("http://ws.correios.com.br/calculador/CalcPrecoPrazo.aspx?nCdEmpresa=08082650&sDsSenha=564321&sCepOrigem=70002900&sCepDestino=" + urls[0] + "&nVlPeso=1&nCdFormato=1&nVlComprimento=20&nVlAltura=20&nVlLargura=20&sCdMaoPropria=n&nVlValorDeclarado=0&sCdAvisoRecebimento=n&nCdServico=04510&nVlDiametro=0&StrRetorno=xml&nIndicaCalculo=3");
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    return data;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String result) {


            Document doc = loadXMLFromString(result);

            String valor = doc.getElementsByTagName("Valor").item(0).getTextContent();
            String prazoEntrega = doc.getElementsByTagName("PrazoEntrega").item(0).getTextContent();

            preco.setText(valor);
            prazo.setText(prazoEntrega);

        }


    }
}
