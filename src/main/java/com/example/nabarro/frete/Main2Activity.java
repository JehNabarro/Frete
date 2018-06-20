package com.example.nabarro.frete;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import cielo.orders.domain.Credentials;
import cielo.orders.domain.Order;
import cielo.sdk.order.OrderManager;
import cielo.sdk.order.ServiceBindListener;
import cielo.sdk.order.payment.PaymentCode;
import cielo.sdk.order.payment.PaymentError;
import cielo.sdk.order.payment.PaymentListener;

public class Main2Activity extends AppCompatActivity {

    // Deixar classe e objeto do lado de fora para poder referenciar to do objeto.
    Credentials credentials;
    OrderManager orderManager;
    ServiceBindListener serviceBindListener;
    Spinner spinner;
    Button btPagar;
    int valor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = getIntent();
        valor = (Integer) intent.getIntExtra("the_value", 0);


        spinner = (Spinner) findViewById(R.id.spinner);
        btPagar = (Button) findViewById(R.id.btPagar);
        btPagar.setEnabled(false);

        btPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), spinner.getSelectedItem() + "", Toast.LENGTH_SHORT).show();
                String opcao = spinner.getSelectedItem() + "";
                Order order = realizaPedido();

                PaymentListener paymentListener = new PaymentListener() {
                    @Override
                    public void onStart() {
                        Log.d("SDKClient", "O pagamento começou.");
                    }

                    @Override
                    public void onPayment(@NotNull Order order) {
                        Log.d("SDKClient", "Um pagamento foi realizado.");
                    }

                    @Override public void onCancel() {
                        Log.d("SDKClient", "A operação foi cancelada.");
                    }

                    @Override public void onError(@NotNull PaymentError paymentError) {
                        Log.d("SDKClient", "Houve um erro no pagamento.");
                    }
                };

                // orderManager.checkoutOrder(order.getId(), order.getPrice(), paymentListener);
                if(opcao.equals("Debito"))
                    orderManager.checkoutOrder(order.getId(), order.getPrice(), PaymentCode.DEBITO_AVISTA, paymentListener);
                else if(opcao.equals("Credito A Vista"))
                    orderManager.checkoutOrder(order.getId(), order.getPrice(), PaymentCode.CREDITO_AVISTA, paymentListener);
                else
                    orderManager.checkoutOrderStore(order.getId(),order.getPrice(), 3, paymentListener);

            }
        });



        // Adicionar credenciais aqui para inciar já criado.
        credentials = new Credentials("9ul8fjWzRuqN", "WWMJ84QH1My7");
        // Alterar o contexto para pegar o contexto da activity.
        orderManager = new OrderManager(credentials, getApplicationContext());


        serviceBindListener = new ServiceBindListener() {

            @Override
            public void onServiceBoundError(Throwable throwable) {
                //Ocorreu um erro ao tentar se conectar com o serviço OrderManager
                Toast.makeText(getApplicationContext(), "Ocorreu um erro ao tentar " +
                        "se conectar com o serviço OrderManager", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceBound() {
                //Você deve garantir que sua aplicação se conectou com a LIO a partir desse listener
                //A partir desse momento você pode utilizar as funções do OrderManager, caso contrário uma exceção será lançada.
                Toast.makeText(getApplicationContext(), "Você deve garantir que sua " +
                        "aplicação se conectou com a LIO a partir desse listener", Toast.LENGTH_SHORT).show();
                btPagar.setEnabled(true);

            }

            @Override
            public void onServiceUnbound() {
                // O serviço foi desvinculado
                Toast.makeText(getApplicationContext(), "O serviço foi desvinculado",
                        Toast.LENGTH_SHORT).show();
            }
        };

        //bind responsavel pela coneção com a LIO.
        orderManager.bind(this, serviceBindListener);


    }

    public Order realizaPedido() {

        Order order = orderManager.createDraftOrder("1");

        // Identificação do produto (Stock Keeping Unit)
        String sku = "2891820317391823";
        String name = "Coca-cola lata";

       // Preço unitário em centavos
        int unitPrice = 1500;
        int quantity = 3;

        // Unidade de medida do produto String
        String unityOfMeasure = "L";

        order.addItem(sku, name, unitPrice, quantity, unityOfMeasure);

        orderManager.placeOrder(order);

        Toast.makeText(getApplicationContext(),"Valor: " + valor, Toast.LENGTH_SHORT).show();

        return order;
    }


}
