import logo from './logo.svg';
import './App.css';
import {Route, Routes} from "react-router-dom";
import Home from "./pages/Home";
import Mailsend from "./pages/Mailsend";
import NotFound from "./pages/NotFound";
import Header from "./component/Header";

function App() {
  return (
    <div className="App">
      <Header/>
      <Routes>
        <Route path="/" element={<Home/>}/>
        <Route path={"/mailsend"} element={<Mailsend/>} />
        <Route path={"*"} element={<NotFound/>}/>
      </Routes>
    </div>
  );
}

export default App;
