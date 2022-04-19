
import { Pagination } from "react-bootstrap";
import './index.css';

interface Amount {
    amount :number
    page: number
    setPage : (num:number) => void

}



export default function PaginationBasic(props:Amount){

    let items = [];
    for (let number = 1; number <= props.amount; number++) {
        items.push(
        <Pagination.Item className="text-secondary"  onClick={() => props.setPage(number)} key={number} active={number === props.page}>
        {number}
        </Pagination.Item>
        );
    };

    return ( 
        <div >
            <Pagination  >
                <Pagination.Prev onClick={() => props.page === 1 ? "" : props.setPage(props.page-1)}/>
                {items}
                <Pagination.Next onClick={() => (props.page < (props.amount) ? props.setPage(props.page+1): "")}/>
            </Pagination> 
        </div>
    );

}