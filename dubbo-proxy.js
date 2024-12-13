var buckets = [1,2,3,5,10,20,30,40,50,60,70,80,90,100,200,300,400,500,1000,2000,3000,4000,5000,10000,Number.POSITIVE_INFINITY]
var latency = new stats.Histogram('dubbo_latency', buckets, ['service'])
var requests = new stats.Counter('dubbo_request', ['service', 'status'])

var $service
var $time

pipy.listen(6666, $=>$
  .decodeDubbo()
  .demux().to($=>$
    .handleMessage(
      msg => {
        var data = Hessian.decode(msg.body)
        $service = data[1]
        $time = Date.now()
      }
    )
    .mux().to($=>$
      .encodeDubbo()
      .connect('10.0.0.2:6666')
      .decodeDubbo()
    )
    .handleMessage(
      msg => {
        var time = Date.now()
        var data = Hessian.decode(msg.body)
        var status = data[0] | 0
        latency.withLabels($service).observe(time - $time)
        requests.withLabels($service, status).increase(1)
      }
    )
  )
  .encodeDubbo()
)