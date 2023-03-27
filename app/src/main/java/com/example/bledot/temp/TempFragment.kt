package com.example.bledot.temp


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bledot.R
import com.example.bledot.databinding.FragmentTempBinding
import com.example.bledot.util.BleDebugLog
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


class TempFragment : Fragment() {

    private val logTag = TempFragment::class.simpleName
    private lateinit var binding: FragmentTempBinding
    private val tempViewModel: TempViewModel by activityViewModels()
    // mqtt 관련
    //private val serverUri = "tcp://mqtt@broker.emqx.io:1883" // 서버 URI (=브로커 URI)
    private val serverUri = "tcp://192.168.1.202:3001" // 서버 URI (=브로커 URI)
    // Publish a message
    private val topic = "mia/topic" // 토픽
    private val qos = 1
    private val retained = false

    private var sendText = ""
    private var receiveText = ""
    // Mqtt 방식의 통신을 지원하는 클래스, MqttAndroidClient 객체 생성
    private lateinit var mqttClient: MqttAndroidClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BleDebugLog.i(logTag, "onCreateView-()")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_temp, container, false)
        with(binding) {
            viewModel = tempViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BleDebugLog.i(logTag, "onViewCreated-()")
        // 연결
        binding.connectBtn.setOnClickListener {
            connectMqtt()
        }
        // 전송
        binding.sendBtn.setOnClickListener {
            sendMessageMqtt()
        }
        // 연결 해제
        binding.disconnectBtn.setOnClickListener {
            disconnectMqtt()
        }
    }

    /**
     * MQTT 연결
     */
    private fun connectMqtt() {
        BleDebugLog.i(logTag, "connectMqtt-()")
        // MqttAndroidClient 초기화
        // → 기본적으로 AUTO_ACT로 동작함 : 메시지가 반환되면 프로세스에 메시지가 도착했다고 즉시 알림
        mqttClient = MqttAndroidClient(requireContext(), serverUri, MqttClient.generateClientId())
        // MqttConnectOptions는 Mqtt의 Client가 서버에 연결하는 방법을 제어하는 클래스
        val connectOptions = MqttConnectOptions()
        connectOptions.isCleanSession = true
        // Mqtt 서버와 연결
        // 연결 결과 콜백 등록 → callbackConnectResult
        mqttClient.connect(connectOptions, null, callbackConnectResult)
    }

    /**
     * connect 결과 처리
     */
    private var callbackConnectResult = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            BleDebugLog.i(logTag, "onSuccess-()  :: 연결 성공")
            BleDebugLog.d(logTag, "asyncActionToken: $asyncActionToken")
            // 연결에 성공하면 해당 토픽 구독
            mqttClient.subscribe(topic, 1)
            mqttCallBack()
            sendMessageMqtt()
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            BleDebugLog.i(logTag, "onFailure-() :: 연결 실패")
            BleDebugLog.e("$exception")
        }
    }

    /**
     * 메시지 전송
     */
    private fun sendMessageMqtt() {
        mqttClient.publish(topic, binding.msg.text.toString().toByteArray(), qos, retained) // 메세지 전송
        sendText = sendText + binding.msg.text.toString() + "\n"
        binding.firstTextView.text = sendText
    }


    /**
     * 메시지 상태 콜백
     */
    private fun mqttCallBack() {
        // 콜백 설정
        mqttClient.setCallback(object : MqttCallback {
            // 연결이 끊겼을 경우
            override fun connectionLost(p0: Throwable?) {
                BleDebugLog.i(logTag, "connectionLost-() :: 연결 끊어짐")
            }

            // 메세지가 도착했을 때
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                BleDebugLog.i(logTag, "messageArrived-() :: 메시지 도착")
                BleDebugLog.d(logTag, "topic: $topic")
                BleDebugLog.d(logTag, "message: $message")
                receiveText = receiveText + message.toString() + "\n\n"
                binding.secondTextView.text = receiveText
            }

            // 메시지 전송이 성공했을 때
            override fun deliveryComplete(p0: IMqttDeliveryToken?) {
                BleDebugLog.i(logTag, "deliveryComplete-() :: 메시지 전송 성공")
            }
        })
    }

    /**
     * MQTT 연결 해제
     */
    private fun disconnectMqtt() {
        BleDebugLog.i(logTag, "disconnectMqtt-()")
        mqttClient.disconnect()
    }
}