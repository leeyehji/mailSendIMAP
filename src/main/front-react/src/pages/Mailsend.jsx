import React, {useState} from 'react';
import axios from "axios";

const Mailsend = () => {
    const [email, setEmail] = useState("");
    const [authCode, setAuthCode] = useState("");
    const [isTrue, setIsTrue] = useState(false);
    const [trueCode, setTrueCode] = useState(null);
    const mailsendURL = "http://localhost:8080/mailSend"
    const authCodeURL = "http://localhost:8080/mailCheck"

    const handleMailSendBtn = async() => {
        try {
            const response = await axios.post(mailsendURL, email, {
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                }
            });
            setIsTrue(response.data);
        }catch (error){
            console.log('Error fetching data: ', error);
        }
    }

    const handleAuthCodeBtn = async() => {
        console.log("인증코드 전송: " + authCode);
        try {
            const response = await axios.post(authCodeURL, null, {
                headers: {
                    'Content-Type': 'application/json; charset=UTF-8'
                },params: {
                    mailAddress: email,
                    authCode: authCode
                }
            });
            setTrueCode(response.data);
        }catch (error){
            console.log('Error fetching data: ', error);
        }
    }
    return (
        <div>
            <h1>메일을 보내봅시다</h1>
            <input type={"text"}
                    placeholder={"이메일을 입력하세요."}
                    onChange={(e)=> setEmail(e.target.value)} />
            <button onClick={handleMailSendBtn} >인증 코드 전송</button><br/>

            {isTrue &&
                <div>
                    <input type={"text"}
                        placeholder={"인증번호를 입력하세요."}
                        onChange={(e)=> setAuthCode(e.target.value)}
                    />
                    <button onClick={handleAuthCodeBtn}>인증코드 확인</button>

                    {trueCode && <h3>인증이 완료되었습니다.</h3>
                        || <h3>인증에 실패하였습니다.</h3>
                    }
                </div>
            }
        </div>
    );
};

export default Mailsend;