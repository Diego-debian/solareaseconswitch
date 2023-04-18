package unal.todosalau.solarease;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private EditText areaEditText;
    private SeekBar inclinationSeekBar;
    private TextView inclinationTextView;
    private Button calculateButton;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener referencias a los elementos del diseño
        latitudeEditText = findViewById(R.id.latitude_edittext);
        longitudeEditText = findViewById(R.id.longitude_edittext);
        areaEditText = findViewById(R.id.area_edittext);
        inclinationSeekBar = findViewById(R.id.inclination_seekbar);
        inclinationTextView = findViewById(R.id.inclination_textview);
        calculateButton = findViewById(R.id.calculate_button);
        resultTextView = findViewById(R.id.result_textview);

        // Configurar listener para el SeekBar de inclinación
        inclinationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                inclinationTextView.setText("Inclinación de los paneles: " + progress + "°");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Configurar listener para el botón de calcular
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validar campos de entrada
                int validationResult = validateInputFields(latitudeEditText, longitudeEditText, areaEditText);

                switch (validationResult) {
                    case 0: // Todos los campos son válidos
                        // Obtener valores de los campos de entrada
                        double latitud = Double.parseDouble(latitudeEditText.getText().toString());
                        double longitud = Double.parseDouble(longitudeEditText.getText().toString());
                        double area = Double.parseDouble(areaEditText.getText().toString());
                        int inclinacion = inclinationSeekBar.getProgress();

                        // Calcular producción de energía y mostrar resultado
                        double produccionEnergia = calcularProduccionEnergia(latitud, longitud, area, inclinacion);
                        resultTextView.setText("Producción de energía: " + produccionEnergia + " kWh");
                        break;

                    case 1: // Algún campo está vacío
                        resultTextView.setText("Por favor, complete todos los campos.");
                        break;

                    default:
                        break;
                }
            }
        });
    }
    // Función para validar campos de entrada
    private int validateInputFields(EditText latitudeEditText, EditText longitudeEditText, EditText areaEditText) {
        if (!latitudeEditText.getText().toString().isEmpty() &&
                !longitudeEditText.getText().toString().isEmpty() &&
                !areaEditText.getText().toString().isEmpty()) {
            return 0; // Todos los campos son válidos
        } else {
            return 1; // Algún campo está vacío
        }
    }
        private double calcularProduccionEnergia(double latitud, double longitud, double area, int inclinacion) {
        // Convertir latitud, longitud e inclinación a radianes
        double latitudRad = Math.toRadians(latitud);
        double longitudRad = Math.toRadians(longitud);
        double inclinacionRad = Math.toRadians(inclinacion);

        // Obtener día del año actual
        int diaDelAnio = LocalDate.now().getDayOfYear();

        // Calcular ángulo de incidencia de la radiación solar
        double anguloIncidencia = Math.acos(Math.sin(latitudRad) * Math.sin(inclinacionRad) + Math.cos(latitudRad) * Math.cos(inclinacionRad) * Math.cos(longitudRad));


        // Calcular radiación solar incidente
        double constanteSolar = 0.1367; // kWh/m²
        double radiacion = constanteSolar * Math.cos(anguloIncidencia) * (1 + 0.033 * Math.cos(Math.toRadians(360 * diaDelAnio / 365.0)));

        // Calcular producción de energía
        double areaPanel = area / 10000.0; // convertir a m²
        double eficienciaPanel = 0.16; // 16% de eficiencia
        double factorPerdidas = 0.9; // pérdida del 10%
        double produccionEnergia = areaPanel * radiacion * eficienciaPanel * factorPerdidas;

        return produccionEnergia;
    }
}
